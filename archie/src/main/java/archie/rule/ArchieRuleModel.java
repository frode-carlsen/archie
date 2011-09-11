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
package archie.rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import archie.builder.ArchieCompilationUnit;

public class ArchieRuleModel {

    private static final String DENY_RULE_NAME = DenyDependencyRule.class.getSimpleName();
    private List<ArchieRule> rules = new ArrayList<ArchieRule>();

    public ArchieRuleModel() {
    }

    public ArchieRuleModel(List<ArchieRule> rules) {
        this.rules.addAll(rules);
    }

    public List<ArchieRule> getRules() {
        return this.rules;
    }

    public void addRule(ArchieRule rule) {
        rules.add(rule);
    }

    @SuppressWarnings("unused")
    public void checkRules(CompilationUnit cu, ArchieCompilationUnit marker) {
        for (ArchieRule rule : getRules()) {
            rule.check(marker);
        }
    }

    public void readProperties(IProject project) throws WorkbenchException {
        File file = getProjectSettingsFile(project);
        if (!file.exists()) {
            return;
        }

        FileReader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new WorkbenchException("Couldn't read settings", e);
        }
        XMLMemento memento = XMLMemento.createReadRoot(reader);
        readDenyRules(memento);
    }

    public void writeProperties(IProject project) {
        XMLMemento memento = XMLMemento.createWriteRoot("archie");
        for (ArchieRule ar : getRules()) {
            if (ar instanceof DenyDependencyRule) {
                writeDenyRule(memento, ar);
            }
        }
        saveMementoToFile(project, memento);

    }

    private void readDenyRules(XMLMemento memento) {
        for (IMemento m : memento.getChildren(DENY_RULE_NAME)) {
            DenyDependencyRule denyRule = new DenyDependencyRule(m.getString("denyFromSrc")
                    , m.getString("denyFrom")
                    , m.getString("denyToSrc")
                    , m.getString("denyTo")
                    , m.getBoolean("enabled"));
            addRule(denyRule);
        }
    }

    private void writeDenyRule(XMLMemento memento, ArchieRule ar) {
        DenyDependencyRule rule = (DenyDependencyRule) ar;
        IMemento child = memento.createChild(DENY_RULE_NAME);
        child.putString("denyFromSrc", rule.getDenyFromSrc() == null ? null : rule.getDenyFromSrc().pattern());
        child.putString("denyFrom", rule.getDenyFrom() == null ? null : rule.getDenyFrom().pattern());
        child.putString("denyToSrc", rule.getDenyToSrc() == null ? null : rule.getDenyToSrc().pattern());
        child.putString("denyTo", rule.getDenyTo() == null ? null : rule.getDenyTo().pattern());
        child.putBoolean("enabled", rule.isEnabled());
    }

    private void saveMementoToFile(IProject project, XMLMemento memento) {
        File stateFile = getProjectSettingsFile(project).getAbsoluteFile();
        if (stateFile != null) {
            try {
                FileOutputStream stream = new FileOutputStream(stateFile);
                OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
                memento.save(writer);
                writer.close();
            } catch (IOException ioe) {
                stateFile.delete();
            }
        }
    }

    private File getProjectSettingsFile(IProject project) {
        IPath path = project.getLocation();
        if (path == null) {
            return null;
        }
        path = path.append("archie-settings.xml");
        return path.toFile();
    }

}
