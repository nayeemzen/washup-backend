package com.washup.app;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WashUpTestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
    value = {DatabaseTestExecutionListener.class},
    mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public abstract class AbstractTest {
}
