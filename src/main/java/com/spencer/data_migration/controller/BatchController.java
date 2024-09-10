package com.spencer.data_migration.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchController {
    private final JobLauncher jobLauncher;
    private final Job dataMigrationJob;

    @PostMapping("start")
    public ResponseEntity<String> startBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt",System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution =  jobLauncher.run(dataMigrationJob,jobParameters);
            return ResponseEntity.ok("Job launched successfully with ID: " + jobExecution.getJobId());
        }catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Job failed with error: " + exception.getMessage());
        }
    }
}
