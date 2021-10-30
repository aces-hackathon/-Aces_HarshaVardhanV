const mongoose = require('mongoose')

var userSchema = new mongoose.Schema({
    username: {
        type: String,
        minLength: 4,
        maxLength: 20,
        required: true,
    },
    password: {
        type: String,
        required: true
    },
    area: {
        type: String,
        required: true,
    },
    city: {
        type: String,
        required: true
    },
    pincode: {
        type: Number,
        minLength: 6,
        maxLength: 6,
        required: true,
    },
    phone: {
        type: Number,
        unique: true
    },
    reports: [
        {
            type: String
        }
    ]
}, {
    timestamps: true
})

module.exports = mongoose.model('User', userSchema)