package cvx;

import java.io.File;
import java.net.URL;

import util.Constants;
import util.Graph;
import vis.DotVisualization;
import vis.comodification.AuthorOrganizationDotVisualization;
import cvx.cvsdata.CVSDatabase;
import framework.Application;
import framework.ApplicationInvokeException;
import framework.Pipe;
import framework.TargetSoftware;

public class CVSApplication extends Application {

    public static void main(String[] args) throws ApplicationInvokeException {
        CVSApplication app = new CVSApplication();
        app.execute(TargetSoftware.EclipseJDT);
    }

    protected void setUp() {
        URL resourcesURL = ClassLoader.getSystemResource("resources");
        File resourcesDir = new File(resourcesURL.getPath());
        System.setProperty("project-path", resourcesDir.getPath()
                + File.separator);
        File rootDir = resourcesDir.getParentFile().getParentFile();
        String outputPath = "";
        if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
            outputPath = "C:\\eclipse-SDK-3.2.1-win32\\eclipse\\workspace\\arch-org\\output\\";
        } else {
            outputPath = new File(rootDir, "output").getPath() + File.separator;
        }
        System.setProperty("output-path", outputPath);

    }

    protected void prepare(TargetSoftware target, Pipe pipe) {
        CVSLogParser parser = CVSLogParserFactory.createCVSLogParser(target);
        parser.parseCVSLogFile();

        CVSDatabase db = CVSDatabase.instance();
        db.load();
        pipe.addData(Constants.KEY_CVS_DATABASE, db);
    }

    protected void analyze(Pipe pipe) {
        CVSDatabase db = (CVSDatabase) pipe.getData(Constants.KEY_CVS_DATABASE);
        if (db == null)
            return;

        CVSAnalysis analysis = new AuthorOrganizationAnalysis();
        // CVSAnalysis analysis = new ModuleOrganizationAnalysis();
        analysis.analyze(db, pipe);
    }

    protected void visualize(Pipe pipe) {
        Graph graph = (Graph) pipe.getData(Constants.KEY_GRAPH);

        if (graph == null)
            return;
        //
        DotVisualization visu = new AuthorOrganizationDotVisualization();
        visu.setGraphType(DotVisualization.UNDIRECTED);
        visu.setAlgorithm(DotVisualization.NEATO);
        visu.setEdgeVisible(false);

        // Visualization visu = new
        // AuthorOrganizationGraphViewerVisualization();
        visu.visualize(graph);
    }
}
