const openpgp = require('openpgp');

export const encrypt = async function(data: string) {
    const {message} = await openpgp.encrypt({
        message: openpgp.message.fromText(data),
        passwords: [process.env['DATABASE_SECRET']],
        armor: false
    });
    const buffer = Buffer.from(message.packets.write());
    return buffer.toString('base64');
}

export const decrypt = async function(cypherText: string) {
    const buffer = new Buffer(cypherText, 'base64');
    const { data: decrypted } = await openpgp.decrypt({
        message: await openpgp.message.read(buffer),
        passwords: [process.env['DATABASE_SECRET']]
    });
    return decrypted;
}
