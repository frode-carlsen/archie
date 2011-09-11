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

import org.eclipse.jdt.core.dom.CompilationUnit;

import archie.builder.ArchieCompilationUnit;

class MockArchieCompilationUnit extends ArchieCompilationUnit {

    volatile TestValidationMessage data;

    public MockArchieCompilationUnit(CompilationUnit cu) {
        super(null, cu);
    }

    @Override
    public void addMarker(String message, int lineNumber, int severity) {
        this.data.message = message;
        this.data.lineNumber = lineNumber;
        this.data.severity = severity;
    }

    @Override
    public String getSourceLocation() {
        return "nowhere";
    }
    
    public void setTestValidationMessage(TestValidationMessage data) {
        this.data = data;
    }
}

class TestValidationMessage {
    String message;
    int lineNumber;
    int severity;
}