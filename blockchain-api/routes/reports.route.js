const reportsRouter = require('express').Router()
const ipfsClient = require('ipfs-http-client')
const fs = require('fs')

const manipulateFiles = require('../utils/manipulateFiles')

const ipfs = ipfsClient.create(new URL('https://ipfs.infura.io:5001'))


reportsRouter.post('/', async (req, res) => {
    const body = req.body
    const { report, accounts } = global.globalVariable

    const file = req.files.img
    const fileName = body.fileName
    const filePath = 'files/'+fileName

    file.mv(filePath, async (err) => {
        if (err) {
            console.log('File transfer failed:', err)
            return res.status(500).json({ err })
        }

        console.log(body)

        const fileHash = await manipulateFiles.addFile(fileName, filePath, ipfs)
        fs.unlink(filePath, (err) => console.log(err))

        const result = await report.reportProblem(fileHash, body.userId, body.desc, body.dept, body.city, false, false, { from: accounts[0] })
        // res.json({ msg: 'Successful', result })
        let {userId, desc, dept} = body
        // res.render('viewReport', { fileHash, userId, desc, dept })
        console.log(fileHash);
        res.json({message: "success"})
    })
})


reportsRouter.get('/', async (req, res) => {
    const report = global.globalVariable.report

    // console.log(report)

    const reportCount = await report.reportCount()
    console.log(reportCount)
    const reports = []

    for(let i=1; i<reportCount; i++) {
        reports.push(await report.reports(i))
    }
    
    // res.json(reports)
    var revReport = reports;
    res.json(revReport.reverse())
})

// reportsRouter.get('/:user', (req, res) => {

// })

reportsRouter.get('/:city', async (req, res) => {
    const { report } = global.globalVariable

    const reportCount = await report.reportCount()
    console.log(reportCount)
    const reports = []

    for (let i = 1; i < reportCount; i++) {
        reports.push(await report.reports(i))
    }

    areaReports = reports.filter((report) => {
        return (req.params.city === report.city)
            ? report
            : null
    })

    res.json(areaReports)
})

// reportsRouter.get('/:dept', (req, res) => {

// })

module.exports = reportsRouter