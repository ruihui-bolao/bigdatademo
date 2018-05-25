package com.example.metricboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.metrics.export.atlas.EnableAtlasMetrics;
import org.springframework.metrics.instrument.MeterRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableAtlasMetrics
public class MetricBootApplication {

/*
	@Bean
	public ThreadPoolTaskExecutor tpExec(MeterRegistry registry){
		ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
		exec.setCorePoolSize(10);
		exec.setMaxPoolSize(10);

		// this demonstrates how to instrument any arbitrary runnable that you run across in application code,
		// but is not strictly necessary here as we have already monitored every task going to the executor above
		// Must be done before initialize
		exec.setTaskDecorator(f -> registry.timer("tp_exec").wrap(f));

		// must be done after initialize(), yikes
		//Change the monitor to watch executor registry.monitor("tp_exec_exec", exec.getThreadPoolExecutor());
		return exec;
	}

	@Bean
	public ThreadPoolTaskScheduler tpSched(MeterRegistry registry){
		ThreadPoolTaskScheduler exec = new ThreadPoolTaskScheduler();
		exec.setPoolSize(10);
		exec.initialize();

		// insofar as a ThreadPoolTaskScheduler @Bean is created for use by @Scheduled, we would NOT
		// attempt to monitor the underlying executor here, and instead put @Timed on the @Scheduled method
		// (or timer.record(..) the contents of the @Scheduled method for more complicated uses)

		// must be done after initialize(), yikes
		registry.monitor("tp_wsched", exec.getScheduledThreadPoolExecutor());
		return exec;
	}
*/

	public static void main(String[] args) {
		SpringApplication.run(MetricBootApplication.class, args);
	}
}
