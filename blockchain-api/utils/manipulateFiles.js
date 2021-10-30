const fs = require('fs')

const addFile = async (fileName, filePath, ipfs) => {
    const file = fs.readFileSync(filePath)
    console.log('file', file, 'filepath', filePath)
    const fileAdded = await ipfs.add({ path: fileName, content: file })
    const fileHash = fileAdded.cid.toString()

    return fileHash
}

module.exports = {
    addFile
}