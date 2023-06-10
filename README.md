- # General
    - #### Team#:fish
    
    - #### Names:Ruomu Zhao
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution: Me


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    WebContent/META_INF/context.xml
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](screen/single_instance_http_1.png)   | 204                         | 189.5                                  | 189.1                        | ??           |
| Case 2: HTTP/10 threads                        | ![](screen/single_instance_http_10.png)   | 435                         | 215.7                                  | 215.4                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](screen/single_instance_https_10.png)   | 509                         | 245.4                                  | 244.3                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](screen/single_instance_http_no_CP_10.png)   | 360                         | 191                                  | 177                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](screen/scaled_instance_http_1.png)   | 206                         | 32                                  | 31                        | ??           |
| Case 2: HTTP/10 threads                        | ![](screen/scaled_instance_http_no_CP_10.png)   | 259                         | 90.9                                  | 90                        | 90.4           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](screen/scaled_instance_http_10.png)   | 253                         | 88                                 | 87                        | ??           |
