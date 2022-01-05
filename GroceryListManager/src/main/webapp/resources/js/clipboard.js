function copy() {
    var copyText = document.querySelector("#formShare:txtLinkToShare");
    console.log("copyText ", copyText);
    copyText.select();
    document.execCommand("copy");
}