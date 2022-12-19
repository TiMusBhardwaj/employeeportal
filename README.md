

# Project Title

Employee management system using Spring State machine.
This implementes problem stated in [challenge.md](https://github.com/TiMusBhardwaj/employeeportal/blob/main/challenge.md)


## BUILD Using Maven Inclues test

```mvn clean package```

## RUN APP
```./mvnw spring-boot:run```

#### Api DOC Reference is available 

http://localhost:8080/swagger-ui/index.html



## API Reference

#### Create Employee

```http
  POST /employee
```


#### Udate employee status

```http
  PUT /employee/${id}/status
```
#### GET employee 

```http
  Get /employee/${id}
```


#### Docker Build

```sudo docker build --platform=linux/x86_64 -t eportal:1.0 . ```


#### Docker Run

```sudo docker run -p 8080:8080 -t eportal:1.0```





