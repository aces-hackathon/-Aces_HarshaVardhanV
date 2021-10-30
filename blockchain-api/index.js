require('dotenv').config()
const express = require('express')
const mongoose = require('mongoose')
const fileUpload = require('express-fileupload')
// const cors = require('cors')

const userRouter = require('./routes/user.route')
const adminRouter = require('./routes/admin.route')
const authRouter = require('./routes/auth.route')

const contractConnect = require('./utils/contractConnect')
const reportsRouter = require('./routes/reports.route')

const app = express()
// app.use(cors)
app.use(express.json())
app.set('view engine', 'ejs')
app.use(fileUpload())

mongoose.connect(process.env.MONGODB_URI, { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => {
        console.log('Connected to MongoDB')
    })
    .catch((error) => {
        console.log('Error connecting to MongoDB:', error.message)
    })

contractConnect()
    .then((res) => {
        global.globalVariable = res
        // console.log(global.globalVariable)
        console.log('Connected to the contract and accounts')
    })
    .catch((error) => {
        console.log('Error connecting to Blockchain:', error.message)
    })

app.get('/', (req, res) => {
    // res.send('<p>Reports api</p>')
    res.render('report')
})

app.get('/reports', (req, res) => {
    res.render('report')
})

app.use('/api/user', userRouter)
app.use('/api/admin', adminRouter)
app.use('/api/auth', authRouter)
app.use('/api/reports', reportsRouter)

app.listen(5000, () => {
    console.log('Server running on port 5000')
})