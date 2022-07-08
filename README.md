# Abstract
With the development of the Internet, more and more people use e-malls to shop. So, how to design and implement a B2C e-commerce system stably and efficiently is a significant issue that needs to be solved today.


Functionally speaking, this project designs and implements three modules: product module, ware module and search module, and realizes the interaction between services with the gateway module. Among them, the module with the most abundent functionalities is the product module, which is related with management of attribute classification, attribute group, brand, platform attributes, and product information. 


Technically speaking, the VUE and Thymeleaf framework are used on the front end. The architecture on the backend is SOA, and the Spring framework and the Java language are chosen for implementation. In addition, MySQL and MybatisPlus are used on the service store to store data. Here are some highlighted technologies: the usage of Spring Cloud components in SOA governance combined with Nginx, which fully demonstrates the advantages of a microservices architecture, including no Single Points of failure, fault isolation, enable the leverage of the heterogeneous technologies, independent and continuous deployments, etc. the usage of Redis to optimize the access speed of three-level classification in the product module, and the usage of ElasticSearch in the search module as search engine.


Finally, The functionality of each module is tested, and the performance is optimized. The test result is that the function are all implemented successfully, and the optimization improves the system performance.

Keywords: e-commerce; Microservice; Spring Framework

