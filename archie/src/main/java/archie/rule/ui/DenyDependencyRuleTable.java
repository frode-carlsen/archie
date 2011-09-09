/*
   Copyright 2011 Frode Carlsen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package archie.rule.ui;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import archie.rule.ArchieRule;
import archie.rule.DenyDependencyRule;

class DenyDependencyRuleTable {

    private static final String ENABLED = "Enabled";
    private static final String TO_MATCH = "Deny To (match)";
    private static final String TO_SRC = "Deny To (src location)";
    private static final String FROM_MATCH = "Deny From (match)";
    private static final String FROM_SRC = "Deny From (src location)";

    private static final String[] TITLES = { ENABLED, FROM_SRC, FROM_MATCH, TO_SRC, TO_MATCH };

    private class MyLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int columnIndex) {
            DenyDependencyRule rule = (DenyDependencyRule) obj;
            switch (columnIndex) {
                case 0:
                    return null;
                case 1:
                    return rule.getDenyFromSrc() == null ? "" : rule.getDenyFromSrc().pattern();
                case 2:
                    return rule.getDenyFrom() == null ? "" : rule.getDenyFrom().pattern();
                case 3:
                    return rule.getDenyToSrc() == null ? "" : rule.getDenyToSrc().pattern();
                case 4:
                    return rule.getDenyTo() == null ? "" : rule.getDenyTo().pattern();
                default:
                    return "";
            }
        }

        @Override
        public Image getColumnImage(Object arg0, int arg1) {
            return null;
        }
    }

    private class MyCellModifier implements ICellModifier {

        @Override
        public boolean canModify(Object element, String property) {
            return true;
        }

        @Override
        public Object getValue(Object element, String property) {
            DenyDependencyRule rule = (DenyDependencyRule) element;
            if (FROM_SRC.equals(property)) {
                return rule.getDenyFromSrc() == null ? null : rule.getDenyFromSrc().pattern();
            } else if (FROM_MATCH.equals(property)) {
                return rule.getDenyFrom() == null ? null : rule.getDenyFrom().pattern();
            } else if (TO_SRC.equals(property)) {
                return rule.getDenyToSrc() == null ? null : rule.getDenyToSrc().pattern();
            } else if (TO_MATCH.equals(property)) {
                return rule.getDenyTo() == null ? null : rule.getDenyTo().pattern();
            } else if (ENABLED.equals(property)) {
                return Boolean.toString(rule.isEnabled());
            } else {
                throw new IllegalArgumentException("Unknown property : " + property);
            }
        }

        @Override
        public void modify(Object element, String property, Object value) {
            if (element instanceof Item) {
                element = ((Item) element).getData();
            }
            DenyDependencyRule rule = (DenyDependencyRule) element;
            if (FROM_SRC.equals(property)) {
                rule.setDenyFromSrc(value == null ? null : Pattern.compile((String) value));
            } else if (FROM_MATCH.equals(property)) {
                rule.setDenyFrom(value == null ? null : Pattern.compile((String) value));
            } else if (TO_SRC.equals(property)) {
                rule.setDenyToSrc(value == null ? null : Pattern.compile((String) value));
            } else if (TO_MATCH.equals(property)) {
                rule.setDenyTo(value == null ? null : Pattern.compile((String) value));
            } else if (ENABLED.equals(property)) {
                rule.setEnabled((Boolean) value);
            } else {
                throw new IllegalArgumentException("Unknown property : " + property);
            }

            viewer.refresh();
        }
    };

    private final TableViewer viewer;
    private final IStructuredContentProvider contentProvider = new ArrayContentProvider();
    private List<ArchieRule> contents;

    DenyDependencyRuleTable(Composite parent, List<ArchieRule> contents) {
        parent.setLayout(new GridLayout(1, false));
        this.contents = contents;
        this.viewer = createTableViewer(parent, contentProvider);
        createButtons(parent);
    }

    Control getControl() {
        return viewer.getControl();
    }

    TableViewer getViewer() {
        return viewer;
    }

    private void createButtons(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout btnLayout = new GridLayout();
        btnLayout.numColumns = 2;
        c.setLayout(btnLayout);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;

        Button add = new Button(c, SWT.PUSH | SWT.CENTER);
        add.setText("Add");
        add.setLayoutData(gridData);
        add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DenyDependencyRule rule = new DenyDependencyRule();
                viewer.add(rule);
                contents.add(rule);

            }
        });
        Button remove = new Button(c, SWT.PUSH | SWT.CENTER);
        remove.setText("Remove");
        remove.setLayoutData(gridData);
        remove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object element = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                viewer.remove(element);
                contents.remove(element);
            }
        });
    }

    private TableViewer createTableViewer(final Composite parent, final IStructuredContentProvider contentProvider) {
        CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(parent
                , SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        createColumns(viewer);
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        viewer.setLabelProvider(new MyLabelProvider());
        viewer.setContentProvider(contentProvider);
        viewer.setInput(contents);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);
        return viewer;
    }

    private void createColumns(final CheckboxTableViewer viewer) {
        String[] titles = TITLES;
        int[] bounds = { 70, 150, 200, 150, 200 };
        Table table = viewer.getTable();

        viewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                DenyDependencyRule rule = (DenyDependencyRule) event.getElement();
                rule.setEnabled(event.getChecked());
            }
        });
        viewer.setCheckStateProvider(new ICheckStateProvider() {

            @Override
            public boolean isGrayed(Object element) {
                return false;
            }

            @Override
            public boolean isChecked(Object element) {
                return ((DenyDependencyRule) element).isEnabled();
            }
        });

        createTableViewerColumn(viewer, titles[0], bounds[0]);
        createTableViewerColumn(viewer, titles[1], bounds[1]);
        createTableViewerColumn(viewer, titles[2], bounds[2]);
        createTableViewerColumn(viewer, titles[3], bounds[3]);
        createTableViewerColumn(viewer, titles[4], bounds[4]);

        viewer.setCellModifier(new MyCellModifier());
        viewer.setColumnProperties(titles);
        CellEditor[] editors = { null
                , new TextCellEditor(table)
                , new TextCellEditor(table)
                , new TextCellEditor(table)
                , new TextCellEditor(table) };
        viewer.setCellEditors(editors);

    }

    private static TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
                SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        return viewerColumn;

    }

}
