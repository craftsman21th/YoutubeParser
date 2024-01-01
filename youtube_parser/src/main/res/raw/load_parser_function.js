/**
 * 判断目标页面是不是已经加载完成了
 */
window.isLoadSuccess = function () {
	if (window?.yt?.config_ != null) {
		// 如果页面有重定向，需要在此判断当前页面是不是目标页
		return true;
	} else {
		return false;
	}
};
/**
 * 获取解密函数
 */
window.getDecryptMethod = async function () {
	return new Promise((resolve, reject) => {
		try {
			const baseJsSrc = 'https://www.youtube.com' + window?.yt?.config_?.PLAYER_JS_URL;
			let xhr = new XMLHttpRequest();
			xhr.open('get', baseJsSrc, true);
			xhr.responseType = 'blob';
			xhr.onreadystatechange = function () {
				if (xhr.readyState === 4) {
					if (xhr.status === 200) {
						const reader = new FileReader();
						reader.onload = function () {
							try {
                                var decryptMethodString = reader.result.match(
                                    /\n.+(function.+a\=a\.split\(\"\"\).+)\n/
                                )[1];
                                const decryptMethodName = decryptMethodString.match(
                                    /a\=a\.split\(\"\"\)\;([a-zA-Z0-9\$]+)\..+/
                                )[1];
                                const hasDollar = decryptMethodName.includes('$');
                                const decryptMethodsReg = new RegExp(
                                    '(var ' + `${hasDollar ? '\\' : ''}` + decryptMethodName + '=\\{(.|\\n)+?\\};)var '
                                );
                                let decryptMethodsInString = reader.result.match(decryptMethodsReg)[1];

								// 定义一个函数用于插入字符串
								var insertString = function (originalString, index, newString) {
									let firstPart = originalString.slice(0, index);
									let secondPart = originalString.slice(index);
									return firstPart + newString + secondPart;
								};

								var methodStartIndex = decryptMethodString.indexOf('{') + 1;
								decryptMethodString = insertString(
									decryptMethodString, methodStartIndex,
									decryptMethodsInString + ';'
								);
								if (decryptMethodString.lastIndexOf(
										';'
									) === decryptMethodString.length - 1) {
									decryptMethodString = decryptMethodString.slice(0, -1);
								}

								var signatureTimestamp = reader.result.match(
									/signatureTimestamp:[0-9]+/
								)[0]?.replace('signatureTimestamp:', '');
								resolve(
									{
										'decryptMethodString': decryptMethodString,
										'signatureTimestamp': signatureTimestamp
									}
								);

								// 将解密函数的包裹对象加载到window，此处只是获取到其序列化值，不需要加载
								// decryptMethodsInString = decryptMethodsInString.replace(
								//     `var ${decryptMethodName}`, `window.${decryptMethodName}`
								// );
								// (new function (decryptMethodsInString))();
							} catch (err) {
								resolve(null);
							}
						};
						reader.readAsText(this.response);
					} else {
						resolve(null);
					}
				}
			};
			xhr.send();
		} catch (err) {
			resolve(null);
		}
	});
};
/**
 * 获取关键参数，并回调给native
 */
window.getKeyInfo = async function () {
	window.getDecryptMethod().then(decryptInfo => {
		let keyInfo = {
			'decrypt_method': '',
			'yt_config': {},
			'signature_timestamp': 0
		};
		try {
			if (decryptInfo?.decryptInfo?.signatureTimestamp !== null
			&& decryptInfo?.decryptMethodString !== undefined
            ) {
				keyInfo.decrypt_method = decryptInfo?.decryptMethodString;
			}
			if (decryptInfo?.signatureTimestamp !== null && decryptInfo?.signatureTimestamp !== undefined) {
				keyInfo.signature_timestamp = decryptInfo?.signatureTimestamp;
			}
			if (yt.config_ !== null && yt.config_ !== undefined) {
				keyInfo.yt_config = JSON.stringify(yt.config_);
			}
		} catch (err) {}
		console.log(keyInfo);
		window.NativeBridge.onGetKeyInfo(JSON.stringify(keyInfo));
	});
};