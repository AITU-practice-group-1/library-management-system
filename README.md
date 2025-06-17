if you wannt to add your own email to send 
add this to application.yml  

CHANGE YOUR_EMAIL adn YOUR_APP_PASSWORD

WARNING YOUT_APP_PASSWORD is application password not just email password 


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
                
