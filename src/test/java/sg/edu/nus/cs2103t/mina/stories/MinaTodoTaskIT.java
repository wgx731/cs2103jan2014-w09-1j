package sg.edu.nus.cs2103t.mina.stories;

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.IDE_CONSOLE;
import static org.jbehave.core.reporters.Format.TXT;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import sg.edu.nus.cs2103t.mina.steps.MinaTodoTaskSteps;

public class MinaTodoTaskIT extends JUnitStory {

    public MinaTodoTaskIT() {
        configuredEmbedder().embedderControls()
                .doGenerateViewAfterStories(true)
                .doIgnoreFailureInStories(false).doIgnoreFailureInView(false)
                .useThreads(MinaITConstant.THREAD_NUM)
                .useStoryTimeoutInSecs(MinaITConstant.TIME_OUT_IN_SECONDS);
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
        return new InstanceStepsFactory(configuration(), new MinaTodoTaskSteps());
    }
}