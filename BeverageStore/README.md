# Beverage Store
A simple Spring Boot application where user can create new beverages (bottle & crate), add the beverages into cart and eventually checkout the cart to place the order. 

## Running The Application
Open the project as a Spring Boot application with the preferred IDE. Run the project, and the application will be available on the following URL.

```
http://localhost:8080/
```

## Demo Data
The project holds some demo data (User, Address, Beverage (Bottle & Crate), Order, Order Item) to start with. More demo data can be added in the DemoData.java file.

## Authentication & Authorization
A simple session based authentication is implemented. Users are required to log in to access the application functionalities.

Currently, the application has no authorization implemented.

Credentials for the demo User. 

```
Username: admin
Password: admin
```

## Application Domain
The application has following domains.

- User - Users are able to use the application.
- Address - An user can have one or multiple addresses. User can add it's address(s) in the application. A user is required to assign billing address & delivery address to order from the collection of it's address(s).
- Beverage - Beverages are the items user can add to cart and order. At this stage, users are able to add new beverages to the system without any authorization. There are two types of beverages.
  - Bottle - Bottle is the basic type of beverage. A bottle has attributes like name, price, volume etc.
  - Crate - A crate is consist of multiple bottle of same type. It has attributes like name, price, the bottle it is consist of, number of bottles in the crate, price etc.
- Cart - The cart is like the basket for shopping. Every user has it's own cart. It can add beverage(s) to the cart and remove them from the cart as well. The cart is user specific and session scoped. Meaning the cart of any user is automatically cleared everytime it's session is dismissed.
    - Cart Item - Cart item is a single item of the cart. A cart item can be a bottle or a crate. It is also allowed to have multiple quantities.
- Order - Order is the finalized form of the cart. An order is created after any user successfully checks out it's cart. An order is required to have both billing address and delivery address. Users can assign these addresses from their pre-existing address collections.
    - Order Item - Order Item is simple the cart item after the checkout.
  
## Design Principals
1. As real world prices are usually float, all the prices are designed as float.
2. In order details page, order items are not retrieved with the order using entity graph, because a second level relationship (order item bottle and crate) retrieving is required.
