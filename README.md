To run this application you need to specify your private credentials 
like data source(postgres), mail credentials

CHANGE YOUR_EMAIL adn YOUR_APP_PASSWORD

WARNING YOUT_APP_PASSWORD is application password not just email password 

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
    