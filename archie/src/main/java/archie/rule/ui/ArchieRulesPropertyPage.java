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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import archie.rule.ArchieRuleModel;

public class ArchieRulesPropertyPage
        extends PropertyPage {

    private final ArchieRuleModel ruleModel = new ArchieRuleModel();

    public ArchieRulesPropertyPage() {
        super();
        setDescription("Defines Archie rules");
    }

    @Override
    protected Control createContents(Composite parent) {
        readProperties();
        return new DenyDependencyRuleTable(parent, this.ruleModel.getRules()).getViewer().getControl();
    }

    public void readProperties() {

        IProject project = null;
        IAdaptable adaptable = getElement();
        if (adaptable instanceof IProject) {
            project = (IProject) getElement();
        } else if (adaptable instanceof IJavaProject) {
            project = ((IJavaProject) getElement()).getProject();
        }

        if (project != null) {
            try {
                ruleModel.readProperties(project);
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean performOk()
    {
        writeProperties();
        return super.performOk();
    }

    @Override
    public void performApply()
    {
        writeProperties();
        super.performApply();
    }

    public void writeProperties() {
        if (getElement() instanceof IProject) {
            ruleModel.writeProperties((IProject) getElement());
        } else if (getElement() instanceof IJavaProject) {
            IJavaProject javaProject = (IJavaProject) getElement();
            ruleModel.writeProperties(javaProject.getProject());
        }
    }

}