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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * !!!! Must be launched using the <b>JUnit Plug-in Test</b> (PDE JUnit) launcher, not the default Junit launcher, since we need
 * access to a workspace in order to resolve classpaths when visting referenced classes.
 * 
 * <p>
 * <b>TIPS</b><br>
 * To avoid popping up an instance of Eclipse and make the test a little bit faster: choose Run / Run Configuration from
 * the menu:
 * <ul>
 * <li>On the Test tab, uncheck "Run in UI thread"</li>
 * <li>On the Main tab, choose application : "[No application] : Headless Mode"</li>
 * </ul>
 * 
 * 
 * @TODO Move to separate plugin
 */
public class DependencyRuleTest {

    @SuppressWarnings("unused")
    private static IJavaProject javaProject;
    
    private static MockArchieCompilationUnit compilationUnit;
    
    private TestValidationMessage validator = new TestValidationMessage();

    @BeforeClass
    public static void setupCompilationUnit() throws Exception {
        javaProject = new EclipseTestProject().getJavaProject();
        
        CompilationUnitFactory compilationUnitFactory = new CompilationUnitFactory();

        compilationUnit = new MockArchieCompilationUnit(
                compilationUnitFactory.createCompilationUnit("hello.world.HelloWorld", ""
                        + "package hello.world;\n"
                        + "import java.util.*;\n"
                        + "import java.math.BigDecimal;\n\n"
                        + "public class HelloWorld{\n"
                        + "   private void hello(){\n"
                        + "      new StringBuilder().append(\"hello\");\n"
                        + "   }\n"
                        + "}")
                );
    }

    @Before
    public void setupValidator() {
        compilationUnit.setTestValidationMessage(validator); // reinitialize
    }

    @Test
    public void shall_trigger_when_match_To_package_and_source_location() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule(".*", ".*", "rt.jar", "java.math.*", true);
        rule.check(compilationUnit);
        assertContains(validator.message, "Import dependency", "java.math.BigDecimal");
        Assert.assertThat(validator.lineNumber, equalTo(3));
    }

    @Test
    public void shall_not_trigger_when_match_To_package_but_not_source_location() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule(".*", ".*", "tools.jar", "java.math.*", true);
        rule.check(compilationUnit);
        assertNull(validator.message);
    }
    
    @Test
    public void shall_not_trigger_when_not_match_From_source_location() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule("test", ".*", ".*", "java.math.*", true);
        rule.check(compilationUnit);
        assertNull(validator.message);
    }
    
    @Test
    public void shall_trigger_when_match_From_package() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule(".*", "hello.world", ".*", "java.math.*", true);
        rule.check(compilationUnit);
        assertNull(validator.message);
    }
    
    @Test
    public void shall_not_trigger_when_not_match_From_package() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule(".*", "bye.world", ".*", "java.math.*", true);
        rule.check(compilationUnit);
        assertNull(validator.message);
    }
    
    @Test
    public void shall_not_trigger_when_matches_From_source_location() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule("src", ".*", "rt.jar", "java.math.*", true);
        rule.check(compilationUnit);
        assertNull(validator.message);
    }

    @Test
    public void shall_trigger_when_match_To_class() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule(".*", ".*", ".*", "java.math.BigDecimal", true);
        rule.check(compilationUnit);
        assertContains(validator.message, "Import dependency", "java.math.BigDecimal");
        Assert.assertThat(validator.lineNumber, equalTo(3));

    }

    @Test
    public void shall_trigger_when_match_To_methodcall() throws Exception {
        DenyDependencyRule rule = new DenyDependencyRule(".*", ".*", ".*", "java.lang.StringBuilder#append.*", true);
        rule.check(compilationUnit);
        assertContains(validator.message, "Method dependency", "java.lang.StringBuilder");
        Assert.assertThat(validator.lineNumber, equalTo(7));
    }

    public static void assertNull(String message) {
        is((String) null).matches(message);
    }

    public static void assertContains(String message, String... expected) {
        not((Object) null).matches(message);
        for (String exp : expected) {
            if (!message.contains(exp)) {
                throw new AssertionError("Unexpected: " + message + "\nExpected: " + exp);
            }
        }
    }

}
