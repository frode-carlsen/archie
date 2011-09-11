package archie.rule;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.junit.Assert;

public class EclipseTestProject {

    private final IJavaProject javaProject;

    public EclipseTestProject() throws Exception {
        javaProject = createJavaProject("P");
    }
    
    public IJavaProject getJavaProject() {
        return this.javaProject;
    }

    private static IJavaProject createJavaProject(String projectName) throws Exception {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        IProject project = root.getProject(projectName);
        project.create(null);
        project.open(null);

        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[] { JavaCore.NATURE_ID });
        project.setDescription(description, null);

        IJavaProject javaProject = JavaCore.create(project);

        IClasspathEntry[] buildPath = {
                JavaCore.newSourceEntry(javaProject.getPath().append("src")),
                JavaRuntime.getDefaultJREContainerEntry()
        };
        javaProject.setRawClasspath(buildPath, project.getFullPath().append("bin"), null);
        javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);

        // create source folder
        IFolder folder = project.getFolder("src");
        folder.create(true, true, null);
        IPackageFragmentRoot srcFolder = javaProject.getPackageFragmentRoot(folder);
        Assert.assertTrue(srcFolder.exists()); // resource exists and is on build path

        return javaProject;

    }

}
