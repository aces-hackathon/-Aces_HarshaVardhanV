const authRouter = require('express').Router()
const User = require('../schema/user.model')
const Admin = require('../schema/admin.model')
const md5 = require('md5')

authRouter.post('/user', async (req, res) => {
    var phone = req.body.phone
    while(phone.length > 10) {
        phone = phone.slice(1)
    }
    req.body.phone = phone

    const body = req.body
    console.log(body)

    const user = await User.findOne({ 
        phone: Number(body.phone)
    })

    if (!user) {
        return res.status(400).json({ error: 'Invalid phone number or password' })
    }

    if (md5(body.password) === user.password) {
        return res.status(200).json(user)
    } else {
        return res.status(400).json({ error: 'Invalid phone number or password' })
    }
})

authRouter.post('/admin', async (req, res) => {
    var phone = req.body.phone
    while(phone.length > 10) {
        phone = phone.slice(1)
    }
    req.body.phone = phone
        
    const body = req.body
    console.log(body)

    const admin = await Admin.findOne({
        $or: [{ phone_1: Number(body.phone) }, { phone_2: Number(body.phone) }]
    })

    if (!admin) {
        return res.status(400).json({ error: 'Invalid phone number or password' })
    }

    if (md5(body.password) === admin.password) {
        return res.status(200).json(admin)
    } else {
        return res.status(400).json({ error: 'Invalid phone number or password' })
    }
})

module.exports = authRouter