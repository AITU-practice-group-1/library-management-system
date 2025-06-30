DESCRIPTION:

This is MVC pattern Based full stack web-application, with main backenbd . Here we have implamented Spring Boot framework with its almost all features. It is the Library Management System, where you can register, search all available and not 
available books in the library, researve them and take for a loan. Furtermore, as a default user you can watch profiles of the other users, see their statistics, create your own list of favourite books. Except Default user, in our system 
librarians are available. They can accept or reject reservations, give a loan, take a loan, sign contracts and give penalties for bad users. Last user type in the system is Admin. They can add, edit, delete books(like librarians also), add,
update and ban users. 
Furthermore, in this app, you can add reviews for the book and report others review with a reason. You can get generated contract with library when you take a loan.


TECHNICAL STACK:


Main framework: Spring Boot, Spring Core and Spring Web               (Model View Controller app);

Variable Validators: Jakarta and Hibernate Validator

Security: Spring Security                                             (Securing endpoints from not allowed access, crsf, cors, and session control(not allowed to enter into one account with different devices in one time))

Database Connection:   Spring Data JPA                                (High level abstract queries for postgresql databse)\

Database: PostgreSQL

Server-Side Caching:  Redis

Message Broker:    Kafka

Containirization:  Docker

Cloud Image Stoore:  Cloudinary

2 Factor Authorization: Google Auth

Document generator:  openhtml

Front-end: Thyemleaf

Logging: slf4j






HOW TO RUN:

To run this application you need to specify your private credentials 
like data source(postgres), mail credentials

CHANGE YOUR_EMAIL adn YOUR_APP_PASSWORD

WARNING YOUR_APP_PASSWORD is application password not just email password 

    spring:
        mail:
            host: smtp.gmail.com
            
            port: 587
            
            username: YOUR_EMAIL 
            
            password: YOUR_APP_PASSWORD
            
            properties:
            
              mail:
              
                smtp:
                
                  auth: true
                  
                  starttls:
                  
                    enable: true
        datasource:
            url: jdbc:postgresql://YOUR_HOST:YOUR_PORT/YOUR_DB_NAME
            
            username: YOUR_DB_USERNAME
            
            password: YOUR_DB_PASSWORD


Also Add Your Email in Custom also in this file

    custom:
      user_email: "your_email"

Also add cloudinary api credentials in this file
    
    cloudinary:
        cloud_name: "your_cloud_name"
        api_key: "your_api_key"
        api_secret: "your_api_secret"
Add data source credentials in this file
    
