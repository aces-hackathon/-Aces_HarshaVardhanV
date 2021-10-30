const userRouter = require('express').Router()
const md5 = require('md5')

const User = require('../schema/user.model')


userRouter.post('/', async (req, res) => {
    try {
        const hash = md5(req.body.password)

        var phone = req.body.phone
        while(phone.length > 10) {
            phone = phone.slice(1)
        }
        req.body.phone = phone

        console.log(req.body)


        const user = await User.findOne({ phone: Number(req.body.phone) })
        if (user) {
            return res.json({"message": "User already exists"});
        }

        const newUser = new User({
            username: req.body.username,
            password: hash,
            area: req.body.area,
            city: req.body.city,
            pincode: Number(req.body.pincode),
            phone: Number(req.body.phone),
            reports: [],
        })

        const savedUser = await newUser.save()
        res.json(savedUser)
    } catch (err) {
        res.status(400).json({"message": "error"});
    }
})

userRouter.get('/', async(req, res) => {
    res.json({
        route: 'User'
    })
})

userRouter.get('/all', async(req, res) => {
    const users = await User.find({})

    res.json(users);
})

module.exports = userRouter