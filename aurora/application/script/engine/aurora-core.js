CompositeMap.prototype.$=function(path,value){
	if(arguments.length==2){
		this.putObject(path,value);
		return value;
	}
	return this.getObject(path);
};

$ctx.getParameter=function(){
	p=this.getChild('parameter');
	if(!p){
		p=this.createChild('parameter');
	}
	return p;
};
$ctx.getModel=function(){
	p=this.getChild('model');
	if(!p){
		p=this.createChild('model');
	}
	return p;
}



function raise_app_error(code){
	throw code;
}

function $bm(model){
	return new ModelService(model);
}

function md5(data){
	return String(Packages.aurora.application.util.MD5Util.md5Hex(data));
}

function des_encrypt(data){
	return String(Packages.aurora.application.util.DesEncrypt.desEncrypt(data));
}

function des_decrypt(data){
	return String(Packages.aurora.application.util.DesEncrypt.desDecrypt(data));
}
