var express     =   require("express");
var app         =   express();
var bodyParser  =   require("body-parser");
var router      =   express.Router();


var mongoOp     =   require("./model/model");

// app.use(express.static(__dirname + "/public"));
// app.get('/contactlist', function(req,res){
// 	console.log('GET Request')
// 	person1 = {
// 		name: 'Anna',
// 		email: 'annamalai@gmail.com',
// 		phone: '90036389'
// 	};
// 	person2 = {
// 		name: 'Jon',
// 		email: 'Jon@gmail.com',
// 		phone: '1233'
// 	};
// 	person3 = {
// 		name: 'Max',
// 		email: 'max@gmail.com',
// 		phone: '4566'
// 	};
// 	var contactlist = [person1,person2,person3];
// 	res.json(contactlist);
// });


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended" : false}));

router.get("/",function(req,res){
    res.json({"error" : false,"message" : "Hello World"});
});

router.route("/users")
    .get(function(req,res){
        var response = {};
        mongoOp.find({},function(err,data){
        // Mongo command to fetch all data from collection.
            if(err) {
                response = {"error" : true,"message" : "Error fetching data"};
            } else {
                response = {"error" : false,"message" : data};
            }
            res.json(response);
        })
    })
    .post(function(req,res){
    var db = new mongoOp();
    var response = {};
    // fetch email and password from REST request.
    // Add strict validation when you use this in Production.
    db.userEmail = req.body.email; 
    // Hash the password using SHA1 algorithm.
    db.userPassword =  require('crypto')
                      .createHash('sha1')
                      .update(req.body.password)
                      .digest('base64');
    db.save(function(err){
    // save() will run insert() command of MongoDB.
    // it will add new data in collection.
        if(err) {
            response = {"error" : true,"message" : "Error adding data"};
        } else {
        	val = "r:"+db._id
            response = {"createdAt" : false,"objectId" : "Data added","sessionToken":val};
        }
        res.json(response);
        });
    });

router.route("/users/:id")
    .get(function(req,res){
        var response = {};
        mongoOp.findById(req.params.id,function(err,data){
        // This will run Mongo Query to fetch data based on ID.
            if(err) {
                response = {"error" : true,"message" : "Error fetching data"};
            } else {
                response = {"error" : false,"message" : data};
            }
            res.json(response);
        })
    })
    .put(function(req,res){
        var response = {};
        // first find out record exists or not
        // if it does then update the record
        mongoOp.findById(req.params.id,function(err,data){
            if(err) {
                response = {"error" : true,"message" : "Error fetching data"};
            } else {
            // we got data from Mongo.
            // change it accordingly.
                if(req.body.userEmail !== undefined) {
                    // case where email needs to be updated.
                    data.userEmail = req.body.userEmail;
                }
                if(req.body.userPassword !== undefined) {
                    // case where password needs to be updated
                    data.userPassword = req.body.userPassword;
                }
                // save the data
                data.save(function(err){
                    if(err) {
                        response = {"error" : true,"message" : "Error updating data"};
                    } else {
                        response = {"error" : false,"message" : "Data is updated for "+req.params.id};
                    }
                    res.json(response);
                })
            }
        })
    })
    .delete(function(req,res){
        var response = {};
        // find the data
        mongoOp.findById(req.params.id,function(err,data){
            if(err) {
                response = {"error" : true,"message" : "Error fetching data"};
            } else {
                // data exists, remove it.
                mongoOp.remove({_id : req.params.id},function(err){
                    if(err) {
                        response = {"error" : true,"message" : "Error deleting data"};
                    } else {
                        response = {"error" : true,"message" : "Data associated with "+req.params.id+"is deleted"};
                    }
                    res.json(response);
                })
            }
        })
    });
app.use('/',router);

app.listen(3001);
console.log("Server running on port 3000");