const Web3 = require('web3')
const contract = require('truffle-contract')
const artifacts = require('../build/contracts/Report.json')

const contractConnect = async () => {
    if (typeof web3 !== 'undefined') {
        var web3 = new Web3(web3.currentProvider)
    } else {
        var web3 = new Web3(new Web3.providers.HttpProvider('http://localhost:7545'))
    }

    const Report = contract(artifacts)
    Report.setProvider(web3.currentProvider)

    const accounts = await web3.eth.getAccounts()
    const report = await Report.deployed()

    return {
        accounts,
        report,
    }
}

module.exports = contractConnect