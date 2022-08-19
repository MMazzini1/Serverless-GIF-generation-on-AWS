
# Serverless GIF Generation app on AWS

- [Introduction](#introduction)
- [Architecture](#architecture)
  - [Application Architechture Diagram](#application-architechture-diagram)
- [Contents of the repository](#contents-of-the-repository)
- [Some comments on different components](#some-comments-on-different-components)
  - [Route53](#route53)
  - [CloudFront + S3 as an origin](#cloudfront--s3-as-an-origin)
  - [API Gateway](#api-gateway)
  - [S3 Event Notifications fan-out with SNS](#s3-event-notifications-fan-out-with-sns)
  - [Lambda](#lambda)
    - [Lambdas in Java?](#lambdas-in-java)
    - [Cold start latency](#cold-start-latency)
- [Improvements](#improvements)

## Introduction

This repository contains the source code of the GIF generation app hosted in:

[https://gifs.martinmazzini.com/](https://gifs.martinmazzini.com/)

This app serves as an example of a serverless architecture integrating different AWS services (API Gateway, Lambda, S3, SNS, CloudFront, Route53). 
The app falls completely under the AWS free tier (excluding Route 53 hosted zones, which cost 0.50 USD per month). As a disclaimer, the app is just an excuse to try different AWS services together and could be made a lot simpler if wanted to. Nonetheless, and depending on the functional and non-functional requirements of a real application, this architecture could make sense.  


## Architecture

### Application Architechture Diagram

The following diagram shows the application architecture with all the components involved. 

![image](https://user-images.githubusercontent.com/25701657/185026361-dbb11641-2919-43a7-b433-7d3d3530279d.png)



The app uses Route 53 as a DNS service. An S3 bucket hosts the static website, served through a CloudFront distribution which acts as a CDN. All subsequent requests go through an API Gateway to connect to the backend. Image upload requests trigger a Lambda function that resizes the image to a fixed size and stores it in an S3 bucket. The creation of the image object in the bucket triggers an S3 event notification with SNS as the target. SNS is necessary for fanning out of the S3 event notification to multiple targets. The targets are three different Lambda functions that generate three different GIFs in parallel. Each lambda function gets the resized image from the bucket, runs a particular image processing algorithm, joins the frames together to build a GIF, and stores the GIF in a second bucket. The front-end gets the GIFs from this bucket, via an API Gateway endpoint, through a simple short-polling mechanism.

## Contents of the repository

 - The **src** folder contains the Java source code of the four different Lambda functions that make up the backend of the application.
 - The **s3-frontend folder** contains the static assets (HTML, JS, CSS) of a very basic, vanilla javascript frontend, which don´t intend to be anything more but the bare minimum needed to get the app working.





## Some comments on different components
The following section contains a more detailed explanation of some of the services used in the application. It doesn´t intend to be a full explanation of any of the services, just some basic introduction or some interesting considerations regarding them

### Route53 

In this project, Route 53 is used for two purposes:

- As a Domain name registrar, through which the domain martinmazzini.com was registered. The process of acquiring a domain with Route 53 is pretty straightforward (though not covered in the free tier). 
- As the DNS service of the application. Route 53 is a highly available, low latency DNS service. 

To use Route 53 as a DNS you have to create a Hosted Zone, which holds the configuration for a specific domain. For each domain, you configure records that map different URLs to different IP addresses. There are different types of records depending on the use case (A, AAAA, CNAME, etc). For this app, just a single record of type “alias” was configured. Alias records are Amazon Route 53-specific extensions to DNS used to route traffic to AWS resources (in this case, to a CloudFront distribution). Alias records are free of charge, but hosted zones cost 0.50 USD per month.

![image](https://user-images.githubusercontent.com/25701657/185034343-b3c7136e-25e6-4c91-9823-a079fb7b1d38.png)


### CloudFront + S3 as an origin

An S3 bucket (with website hosting enabled) serves the front end of the app. To enable website hosting, you need to enable it at the bucket level, specify an index document for the root domain, disable the *Block public access setting* on the bucket, and also add a bucket policy that allows getting requests on the bucket for any principal. The process is explained in the following link:
https://docs.aws.amazon.com/AmazonS3/latest/userguide/EnableWebsiteHosting.html

The S3 bucket sits behind a CloudFront distribution, which functions as a CDN. CDNs cache content at AWS edge locations, close to users, thereby reducing latency. It also makes it possible to connect to the front end via HTTPS  (S3 hosting-enabled buckets are only capable of HTTP by themselves). For the HTTPS connection, first, an SSL certificate has to be created with the ACM service. The configuration of this whole setup (Route53 + CloudFront + S3) is pretty straightforward and is explained on the following link:
https://docs.aws.amazon.com/AmazonS3/latest/userguide/website-hosting-custom-domain-walkthrough.html

### API Gateway

The front-end requests are routed through an API Gateway, which acts as a reverse proxy for accessing different backend services. It also offers lots of other useful features, such as authorization, rate limiting, API keys management, API versioning, transformation, and validation of requests and responses (via mapping templates and models).

In this project, API Gateway exposes just two endpoints. The first one is used for uploading an image to an S3 bucket, and the second one is for getting the generated GIFs from the other bucket. When integrating API Gateway with S3, it´s possible to do it in two ways. You can either use a Lambda Function that saves the object to S3 or directly integrate the endpoint with the S3 bucket, without any lambda function in between.

![image](https://user-images.githubusercontent.com/25701657/185026401-dc8db11f-afc0-49b7-857d-9d34a70fd157.png)


For the upload scenario, it makes sense to use an intermediary Lambda that can resize the image before saving it to S3. The resizing is useful for making sure that the final GIFs have a standard size, and that the file size doesn´t get too big. API Gateway has a maximum payload of 10Mb and Lambda of 6Mb, so it´s important that the final GIF size is not too big. The following resource has a good explanation for setting up a Lambda proxy endpoint to process a multipart request, to upload the image.

https://medium.com/swlh/processing-multipart-form-data-using-api-gateway-and-a-java-lambda-proxy-for-storage-in-s3-e6598033ff3e

For the GIF download endpoint, there´s no need for a lambda function in between, so in this case, the integration is done directly with S3 using an AWS integration type endpoint:

https://docs.aws.amazon.com/apigateway/latest/developerguide/integrating-api-with-aws-services-s3.html

### S3 Event Notifications fan-out with SNS
S3 buckets can publish notifications when an object gets created/updated/deleted in a specific bucket (among others). They can trigger a Lambda function, post a message in an SQS queue or publish to an SNS topic, as shown in the following image.

![AWS drawio (10)](https://user-images.githubusercontent.com/25701657/185527026-cf0bdf56-f69a-4444-abbb-15e86274d5d9.png)



For a given S3 event, only a single target can be configured (one SNS topic, one SQS queue, or one Lambda function). Because we need to deliver the notification to three different Lambda functions (to generate the three GIFs in parallel) we must use an SNS topic as the target. SNS, being a pub-sub service, will allow us to fan-out the S3 event notification. Each Lambda Function subscribes to the SNS topic and will receive the S3 event as part of the SNS message (with an asynchronous invocation type). The following image shows this configuration.

![AWS drawio (12)](https://user-images.githubusercontent.com/25701657/185527125-8ad71f76-1310-4d2b-b4ed-f1e9a4dc9f79.png)

Another common pattern for fan-out (not used in this project) is using SNS + SQS together. With this pattern, apart from the fan-out capabilities, we would get the advantages of SQS (buffering of messages, optional batch processing, delays, longer retention period). With SNS, if Lambda exceeds the configured retries amount, the message would get lost. With SQS this would only happen if the retention period expires.

![image](https://user-images.githubusercontent.com/25701657/185026592-419dc3d0-712f-4586-9685-4d864ce2b9c4.png)


### Lambda

The backend of the application is built with four Lambda functions. One Lambda function is responsible for image upload, and the other three generate the GIFs in parallel.

#### Lambdas in Java?
Java might not be the most common language for Lambda functions. In this project, it was mainly chosen as a matter of convenience, because the GIF generation functions had already been written in Java before having decided to even make this project.
The following articles offer some good information regarding the most common languages used in Lambda functions, and the pros and cons of using Java in Lambdas, respectively.

https://www.datadoghq.com/state-of-serverless 

https://www.cockroachlabs.com/blog/java-and-aws-lambda/

#### Cold start latency
The cold start latency of the Lambda functions proves to be significant. This is easily tested by comparing the time the app takes to generate GIFs for the first time vs an immediate second time (while the AWS Lambda is still warm). In a production environment, it could be reduced by using Provisioned Concurrency (a relatively new feature to reduce cold start latency)


## Improvements 
There are some things that could be done to improve this app that were left undone:

 - Replace the short polling mechanism for something like SSE or Websockets (API Gateway supports the second).
 - Validate image file size and file type in the backend (because front-end only validation is not at all reliable).
 - Use Infrastructure as code (Terraform or ClouFormtion) and/or SAM to create the infrastructure.
 - Using provisioned concurrency for reducing Lambda cold-start times
 https://docs.aws.amazon.com/lambda/latest/dg/provisioned-concurrency.html#optimizing-latency
 
