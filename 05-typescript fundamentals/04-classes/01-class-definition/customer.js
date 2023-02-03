var Customer = /** @class */ (function () {
    function Customer(theFirst, theLast) {
        this.firstName = theFirst;
        this.lastName = theLast;
    }
    return Customer;
}());
// let's create an instance
var myCustomer = new Customer("Martin", "Dixon");
// myCustomer.firstName = "Martin";
// myCustomer.lastName = "Dixon";
console.log(myCustomer.firstName);
console.log(myCustomer.lastName);
