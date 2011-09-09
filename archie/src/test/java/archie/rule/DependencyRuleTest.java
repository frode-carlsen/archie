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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

/**
 * Use cases:
 * 
 * - Class/Package depends on Class/Package - deny
 * - Class/Package/Methods depends on Class/Package/Method - deny
 * 
 */
public class DependencyRuleTest {

    @SuppressWarnings("unused")
    @Test
    public void shall_trigger_when_match_package() throws Exception {
        String dummy = "import java.util.*;\n import java.math.BigDecimal;\n\npublic class HelloWorld{}";
        CompilationUnit unit = createCompilationUnit(dummy);

        // when(packageName().matches("java.util")).assertThat(doesNotDependOn())

    }

    private CompilationUnit createCompilationUnit(String dummy) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        parser.setResolveBindings(true);
        parser.setSource(dummy.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu;
    }
}
