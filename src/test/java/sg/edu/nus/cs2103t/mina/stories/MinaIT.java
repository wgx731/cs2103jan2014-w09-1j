package sg.edu.nus.cs2103t.mina.stories;

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.IDE_CONSOLE;
import static org.jbehave.core.reporters.Format.TXT;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import sg.edu.nus.cs2103t.mina.steps.MinaAddTaskSteps;

public class MinaIT extends JUnitStories {

    private static final String TEST_RESOURCE_ROOT = "src/test/resources";
    private static final String STORIES_PATH_PATTEN = "sg/edu/nus/cs2103t/mina/stories/*.story";
    private static final int THREAD_NUM = 1;
    private static final int TIME_OUT_IN_SECONDS = 60;

    public MinaIT() {
        configuredEmbedder().embedderControls()
                .doGenerateViewAfterStories(true)
                .doIgnoreFailureInStories(false).doIgnoreFailureInView(false)
                .useThreads(THREAD_NUM)
                .useStoryTimeoutInSecs(TIME_OUT_IN_SECONDS);
    }

    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withDefaultFormats().withFormats(CONSOLE, TXT,
                                IDE_CONSOLE, HTML));
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), new MinaAddTaskSteps());
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(
                CodeLocations.codeLocationFromPath(TEST_RESOURCE_ROOT),
                STORIES_PATH_PATTEN, "");
    }
}