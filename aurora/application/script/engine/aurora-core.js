CompositeMap.prototype.$=function(path,value){
	if(arguments.length==2){
		this.putObject(path,value);
		return value;
	}
	return this.getObject(path);
};

function raise_app_error(code){
	throw code;
}

function $bm(model,option){
	var bm=new ModelService(model);
	if(arguments.length==2 && option) bm.option=option;
	return bm
}

function $instance(cls){
	return Packages.aurora.application.script.scriptobject.ScriptUtil.getInstanceOfType(cls);
}

function $cache(name){
	var cacheF = $instance('uncertain.cache.INamedCacheFactory');
    return cacheF.getNamedCache(name);
}

function $config(){
	importClass(Packages.uncertain.composite.CompositeUtil);
    var svc = Packages.aurora.service.ServiceInstance.getInstance($ctx.getData())
    return svc.getServiceConfigData().getRoot();
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
