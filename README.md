# Spring Cloud Circuit Breaker Demo

This application is intended to be a reference point for application setup and usage
of [Spring Cloud Hystrix](https://github.com/Netflix/Hystrix/wiki) as a Circuit Breaker implementaion for a Spring Boot
Microservice Architecture.  

## Assumptions
This repository assumes you have knowledge of the [Pivotal Cloud Foundry CLI](https://docs.cloudfoundry.org/cf-cli/getting-started.html) and 
access to a running instance of [Pivotal Application Service](https://run.pivotal.io/) such as Pivotal Web
Service or an On-Prem installation. If you do not have access to an existing instance
you can sign up for a no-committal [trial with Pivotal directly](https://try.run.pivotal.io)

## Building the Demo
This reference repository contains 2 modules.  One for [the producer application](Circuit-Breaker-Book-List)
and one for [the consumer application](Circuit-Breaker-To-Read-List).  Each module is
packaged with [Maven](https://maven.apache.org/) build tools including [Maven Wrapper](https://www.baeldung.com/maven-wrapper). Each application can
individually built from the base repository file by running the following commands using 

##### Bash (Mac & Linux) 
```bash
cd Circuit-Breaker-Book-List
./mvnw clean package

cd ..

cd Circuit-Breaker-To-Read-List
./mvnw clean package
```

This generates your build files using maven to manage the dependencies.  Using the Maven
wrapper ensures you do *not* need to have a local installation of Maven. This step is 
automatically performed by the build script outlined below.

## Deploying to Cloud Foundry
We have provided a simple bash script that will automatically push the applications to
your space in PCF.  The `deploy.sh` script does require you to first Login to
your cloud foundry instance.  

Once you have logged in and targeted your space.  Verify your target using 

##### Bash (Mac & Linux)
```bash
cf target
```
to ensure you are connected to the correct org and space. Then, from the base directory 
of the repository run the following:

##### Bash (Mac & Linux)
```bash
./deploy.sh
```

This command will build each application, push to your org in cloud foundry and provision
and request your Circuit Breaker instance which will be bound to each application.

## Understanding the Hystrix Configuration
Spring Cloud Circuit Breaker can be configured very simply through an Annotation.  The 
`@HystrixCommand()` annotation provides you a simple wrapper around your method with the
default circuit breaker settings.  The Fallback method you provide is called in the event
of an open circuit outage.  

### Fallback Methods
In [our example](BookService.java)
you are able to see we use a fallback method of `reliable` in the `@HystrixCommand()` annotation. 
This method uses no network calls and returns a static, context relevant message in the event
of an outage to provide a continued valuable user experience when our down-stream dependency
fails. This is a reccommended best practice : When using a circuit breaker to protect yourself from
dependency failur you should always have a fallback path that can be executed independently of *any*
downstream system.  

If desired, you can use a fallback method that can be another Hystrix Command.  For example: Imagine you
have a cloud instance of mySql and that begins to fail.  You have implemented a Circuit Breaker on that
command and provided a primary fallback method that makes a SOAP call to an on premise solution to fetch
the data reqired to continue to serve your clients.  That method is even wrapped in a circuit breaker to ensure
that if your secondary path fails you still have coverage. That third method could be another circuit breaker call
to another system (you can chain hystrix paths indefinitely, though we suggest no more than 2) and that your
final fallback method should be entirely indepodent and rely on nothing other than your own system. 

### Command Key
The `commandkey` used in the annotation is used to identify the call in what is known as a 
command group in Hystrix.  This type of grouping allows you apply configuration thresholds
to groups of commands together.  We only have one interaction with this dependency and therefore
only one command falls into this group.  The power of commandkeys comes into play when working
in a heavier complexity system.  
For example: You may have several cloud based dependencies in which you have
a timeout of 10 seconds and a snapshot window of 30 seconds.  You may also have an on-prem 
dependency legacy system that may need timeout thresholds much higher (120 seconds or more)
and may need much longer to recover than other cloud based dependencies. Using commandkeys
provides configuration based setup for these groups.

### Property Based Setup (Application Properties)
Our demo also provides a convenient point of reference for [application property](/Circuit-Breaker-To-Read-List/src/main/resources/application.yml) 
based setup.  This list is not exhaustive but provides simplistic examples of the extensibility
of hystrix to support multiple failure thresholds, timeout styles, evaluation windows, and isolation strategies.

#### Property based setup command key interpolation
One item we would like to call attention to is that spring utilizes the `commandkey` when evaluating the branch
of properties to use for any one command.  We have a command key group of `booklistcall` that has all of its specific properties located here: 

```yaml
hystrix:
  booklistcall:
    (Properties specific to HystrixCommands that are grouped under the booklistcall group)
```

### Further Reading 
This repository and demo are based off the Spring Cloud Circuit Breaker demo application and the 
Hystrix Wiki on Github. Please refer to those sources for a deeper understanding of the power of 
circuit breaker patterns. Additionally you can review the [attached slides](/SpringCloudCircuitBreaker.pdf) used during the presentation.

- [Circuit Breaker Patterns](https://martinfowler.com/bliki/CircuitBreaker.html)
- [Spring Cloud Circuit Breaker Demo](https://spring.io/guides/gs/circuit-breaker/)
- [Hystrix Github WIKI](https://github.com/Netflix/Hystrix/wiki)
