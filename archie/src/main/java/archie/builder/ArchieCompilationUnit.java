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
package archie.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ArchieCompilationUnit {

    private static final String MARKER_TYPE = "archie.archieProblem";

    private final IFile file;
    private final CompilationUnit cu;

    public ArchieCompilationUnit(IFile resource, CompilationUnit cu) {
        this.file = resource;
        this.cu = cu;
    }

    public void addMarker(String message, int lineNumber, int severity) {
        try {
            IMarker marker = file.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public CompilationUnit getCompilationUnit() {
        return this.cu;
    }

    public String getPackageName() {
        return cu.getPackage() == null ? "default" : cu.getPackage().getName().getFullyQualifiedName();
    }

    public String getSourceLocation() {
        return cu.getTypeRoot().getResource().getProjectRelativePath().toPortableString();
    }

    public String getFullyQualifiedClassName() {
        return getPackageName() + "." + cu.getTypeRoot().getElementName();
    }

    public void accept(ASTVisitor visitor) {
        getCompilationUnit().accept(visitor);
    }
}
