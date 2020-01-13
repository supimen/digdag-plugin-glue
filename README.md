[![CircleCI](https://circleci.com/gh/supimen/digdag-plugin-glue/tree/master.svg?style=svg)](https://circleci.com/gh/supimen/digdag-plugin-glue/tree/master)

# digdag-plugin-glue

# Overview

- Plugin type: operator

# Usage
```yaml
_export:
  plugin:
    repositories:
      - https://jitpack.io
    dependencies:
      - com.github.supimen:digdag-plugin-glue:0.1.0
  region: ap-northeast-1

+crawler:
  glue.start_crawler>:
  crawler_name: sample-crawler

+job_run:
  glue.start_job_run>:
  job_name: sample-job-run
  arguments:
    --param1: "value1"
    --param2: "value2"
  max_capacity: 10
```

See [examples](./example/example.dig)

# Common Configuration
### Secrets
- glue.access_key_id: AWS access key ID (required)
- glue.secret_access_key_id: AWS Secret Access Key (required)

### Options
- region: The AWS region to use for Glue. (string, optional)

# Configuration for glue.start_crawler>
### Options
- crawler_name: The name of the crawler. (string, required)

# Configuration for glue.start_job_run>
### Options
- job_name: The name of the job. (string, required)
- arguments: The key-value pairs that are passed as arguments to the script invoked by the job. The key name is prefixed with "--". (object of key-value pairs, optional)
- max_capacity: The number of AWS Glue DPUs. (double, optional)
- timeout: The JobRun timeout in minutes. (integer, optional)
- worker_type: The type of predefined worker that is allocated when a job runs. (string, optional)
- number_of_workers: The number of workers of a defined workerType that are allocated when a job runs. (integer, optional)
- notify_delay_after: The number of threshold (in minutes) before a delay notification is sent when a job runs. (integer, optional)
- security_configuration: The name of the SecurityConfiguration structure to be used with this job run. (string, optional)

# Development
TODO


