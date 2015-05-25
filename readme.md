## GoSafe readme
=========

### Code Package Organization

-Backend

    -APIServices

        Coordinate.java

        Geocoding.java

    -DataMiner

        FrequentItems.java

        ItemSet.java

        Mine.java

        WordID.java

    -DBManager

	ImportCrimeRequest.java

	ImportRulesRequest.java

	QueryCrimeRequest.java

	QueryRulesRequest.java

	QueryUsersRequest.java

	UserRegistrationRequest.java

    -Publisher

        PostToTwitterRequest.java

        SendEmailRequest.java

    -Crimes.java

    -Registration.java

    -ReportCrime.java

    -Rules.java

    -Search.java

-Front

    -css  <!-- customized css style definitions -->
        -font-awesome <!-- TO BE REMOVED --> 

    -font-awesome  <!-- template css style definitions -->

    -fonts  <!-- font style definitions -->

    -img
        -markers <!-- self-designed markers for types of crimes -->

    -js <!-- self-defined and template javascript files -->

    -less <!-- css templates for 'less' style  -->

    -mail <!-- php scripts for mail service -->
    index.html <!-- the landing page of our website -->
    info.html <!-- user subscription and information feed webpage -->
    LICENCE
    map.html  <!-- crime-map page  -->
    READEME.md

    -sample <!--   TO BE REMOVED -->

-JavaML  <!--  TO BE REMOVED -->


### Front-end implementation

-Front

    -css:
        bootstrap.css <!-- css styles defined for the bootstrap template -->
        bootstrap.min.css  <!-- css styles defined for the bootstrap template -->
        bootstrap.min1.css <!--    TO BE REMOVED -->
        bootstrap.min2.css <!--    TO BE REMOVED -->
        freelancer.css  <!-- css styles defined for the freelancer theme template -->
        video.css  <!-- css style setting for our embeded video environment -->
        font.css <!-- font setting for the template -->
        map.css <!-- the crime-map environment defined for our web-application -->
        ...  <!-- auxiliary css styles -->

    -font-awesome: <!-- font style template used for our web application -->

    -fonts  <!-- font style definitions -->

    -img
        -markers <!-- self-designed markers for types of crimes -->

    -js
        bootstrap.js, bootstrap.min.js <!--  javascript libs for bootstrap template -->
        freelancer.js  <!-- javascript libs for freelancer tempalte  -->
        index_functions.js  
            our customized javascript functions for reporting crimes information to our system and generating the confirmation page for the report.

                - mySearch(): interface for search zip-code and return the crime-map for the region of that zipcode

                - myReport(): generate the report confirmation page based on the crime information submitted by the user 
         
        jquery.js <!-- jquery javascript libs -->
        video.js <!-- background video playback javascript function  -->
        ...  <!-- auxiliary js files-->

    -less  <!-- less library for website styles -->

    -mail <!-- php scripts for mail service -->

    index.html 
        the landing page of our website, which includes following sections: header, navigation bar, crime report entry and shortcut for regions. 
        
	- navigation bar: includes shortcuts for regional crime-information, crime subscription alert and the zipcode-lookup functinality defined by `mySearch()` javascript. 
        
	- crime report entry: provides interface for users to submit formatted information of the crime, date, time, address, zipcode and particular crime type. Once the `report crime` button is clicked, the `myReport()` function is envoked and corresponding fields of the confirmation page is automatically filled the submitted information. Crime description is optional before the user finally submits the crime information to our system.
        
        - checkDate() used to check the date validity of the reported crime. if the submitted date is later than current date, return a warning alert; o/w return a success alert.
    
    info.html
        the page to display the most recent crimes and crime patterns of particular areas of users' interest. After logging on the website, users can submit
        their address to subscribe most recent crime and data mining results.
        
	- statusChangeCallback(response)
            The response object is returned with a status field that lets the app know the current login status of the person. Three status are possible:
            // 1. Logged into your app ('connected')
            // 2. Logged into Facebook, but not your app ('not_authorized')
            // 3. Not logged into Facebook and can't tell if they are logged into your app or not.
            once the status is `connected`, then use checkUser() to send relative information via servlet to the backend.

        - checkUser(): set up new http-request and send facebook user's id, first, last names and email address to the backend, in order to fetch his subscription information in our database.

        - updateAddress(): send user's address to backend database via servlet.

        - getCrimes(), getRules(): obtain recent crimes and association crime patterns of users' interest and display on the webpage.

    map.html
         display crime informaton on the map centered on users' interest. 
         
	- query(): the interface for searching the backend database for crimes of given url.queryValue.

         - resetCenter(location): set the map to the center of the region covered by a  particular zipcode.

         - reqListener(): mark the crime of respective types 

        

### Back-end implementation


-Backend

    - APIServices:
        
	Coordinate.java: define the coordinate class for google map display
        
	Geocoding.java: define the Geocoding class that format the user submitted address of crime into a Json object.
         
	    - Geocoding(address): get the geolocation information like longitude and latittude of the String `address` submitted by user; 
         
	    - get...(): get related information from the Json object defined by Geocoding class.
    
    - DataMiner:

        - FrequentItems.java: find out the association rules based on the Apriori algorithm of `frequent item sets`  (ref: https://www.it.uu.se/edu/course/homepage/infoutv/ht08/vldb94_rj.pdf)
         
            -findFrequentPairs(): find out factors that are frequently linked to observed crimes, i.e. the correlation between date, time, location and a particular type of crime. Return the frequent pairs with higher frequency than the give threshold. (refer to code for implemenation details)

            -findRules(): find strongly correlated factors among frequent item sets. Store the rule as following format: LHS --> RHT with `Support` and `Confidence`; Format such information into a Json object. 

            -print(): print such rules into screen as the data mining result
            
            -printfile(): save such rules into a file

        - ItemSet.java: defines a data-structure (class) to store frequent itemsets used in a-priori algorithm. 
        

        - Mine.java: pre-process the data-set for the data mining task and save the pre-processed data to a new file.
           
            - main(): parse each field of each json object; divide time of a day into `morning`, `afternoon`,  `evening` and `night` categories according to following rule:
                Morning: 5am - 12pm
                Afternoon: 12pm - 5pm
                Evening: 5pm - 9pm
                Night: 9pm - 5am (next day)

            set up the threshold for support and confidence
            print any rules with RHS result

         - WordID.java: define a class to convert file to FrequentItem set and calculate its size


    - DBManager:

        ImportCrimeRequest.java: a class to handle user submitted crime information at the backend. Store valid information into the dynamo DB and reject invalid data with an error message.
       
            - ImportCrimeRequest() : a constructive function that defines basic parameters to access dynamoDB

            - importCrime(): take input as Json crime of user's submitted information and try to store it in the dynamoDB. It first checks the existence of every essential fields and then calls geocoding to translate the address string to a Json format address.

        ImportRuleRequest.java: a class to save data mining rules in a table created in dynamoDB
	
	    - ImportRulesRequest(): constructor

            - createTable(): create a table in dynamoDB to save data-mining rules; with fields including rhs, lhs, support and confidence

            - importRules(rules): given a mining rule, put it into the table saving data-mining rules

        QueryCrimeRequest.java: a class to scan crime history table in dynamoDB and output a JSON array.
	
	    - QueryCrimeRequest(): constructor

            - getCrimes(): scan the table and return a JSONArray of crimes information relative to the query

            - matches(crime): verify each section of the submitted crime information, o/w return false

	    - shutdown(): shut down dynamoDB

        QueryRuleRequest.java: a class to scan crime rule table in dynamoDB and output a JSON array.

            - QueryRuleRequest(): constructor

            - getRules(): scan the table and return a JSONArray of crime rule

	    - shutdown(): shut down dynamoDB

	QueryUsersRequest.java: a class to scan user table in dynamoDB

	    - QueryUsersRequest(): constructor

	    - getUsers(): scan the table and get the user by zipcode

	    - getZipCode(): fetch the address with given user id and convert the address to zipcode

	    - shutdown(): shut down dynamoDB

        UserRegistrationRequest.java: a class responsible for generating and maintaining user information in dynamoDB.

            - UserRegistrationRequest(): constructor

            - register(): put the user into the database

            - updateAddress(): query the database with given id and update the address field

            - exists(): check the existence of a certain user

	    - shutdown(): shut down dynamoDB

    - Publisher:

       PostToTwitterRequest.java: a class to handle Twitter posts for crime report.
       
            - PostToTwitterRequest(): constructor
		
            - call(): update twitter status for the crime report

       SendEmailRequest.java: a class to send email for crime report
           
            - SendEmailRequest(): constructor

            - call(): send email to corresponding users for crime report

    - Crimes.java: a servlet to parse the returned response of crime information from backend.

    - Registration.java: a class to register user submitted information to backend system.

    - ReportCrime.java: a class to record all the information submitted by user and append such information into the database.

    - Rules.java: a class to generate association rules of the factors

    - Search: a class to support the search function at front-end.
