const mongoose = require('mongoose')

var adminSchema = new mongoose.Schema({
    department: {
        type: String,
        minLength: 4,
        maxLength: 20,
        required: true,
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
    password: {
        type: String,
        required: true
    },
    phone_1: {
        type: Number,
        unique: true
    },
    phone_2: {
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

module.exports = mongoose.model('Admin', adminSchema)