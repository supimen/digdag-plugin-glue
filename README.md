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
      - com.github.supimen:digdag-plugin-glue:0.1.1
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
### System Options
- auth_method: name of mechanism to authenticate requests (basic, env, instance, profile, or properties. default: basic)
- "basic": uses the following secrets values to authenticate.
    - glue.access_key_id: AWS access key ID (string, required)
    - glue.secret_access_key_id: AWS secret access key (string, required)
- "env": uses AWS_ACCESS_KEY_ID (or AWS_ACCESS_KEY) and AWS_SECRET_KEY (or AWS_SECRET_ACCESS_KEY) environment variables.
- "instance": uses EC2 instance profile.
- "profile": uses credentials written in a file. Format of the file is as following, where [...] is a name of profile.
    - profile_file: path to a profiles file. (string, default: given by AWS_CREDENTIAL_PROFILES_FILE environment varialbe, or ~/.aws/credentials).
    - profile_name: name of a profile. (string, default: "default")
        ```
        [default]
        aws_access_key_id=YOUR_ACCESS_KEY_ID
        aws_secret_access_key=YOUR_SECRET_ACCESS_KEY

        [profile2]
        ...
        ```
- "properties": uses aws.accessKeyId and aws.secretKey Java system properties.

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


