const adminRouter = require('express').Router()
const md5 = require('md5')

const Admin = require('../schema/admin.model')


adminRouter.post('/', async (req, res) => {
    const hash = md5(req.body.password)

    var phone = req.body.phone_1
    while(phone.length > 10) {
        phone = phone.slice(1)
    }
    req.body.phone_1 = phone

    phone = req.body.phone_2
    while(phone.length > 10) {
        phone = phone.slice(1)
    }
    req.body.phone_2 = phone

    const newAdmin = new Admin({
        department: req.body.department,
        password: hash,
        area: req.body.area,
        city: req.body.city,
        pincode: Number(req.body.pincode),
        phone_1: Number(req.body.phone_1),
        phone_2: Number(req.body.phone_2),
        reports: [],
    })

    res.json(newAdmin)

    const savedAdmin = await newAdmin.save()
    res.json(savedAdmin)
})

adminRouter.get('/', async (req, res) => {
    res.json({
        route: 'Admin'
    })
})

module.exports = adminRouter