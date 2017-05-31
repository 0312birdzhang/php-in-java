package net.collegeman.phpinjava;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.EnvVar;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;

/**
 * @author debo.zhang
 *
 */
public class MyTest {

	private static final String yh = "\"";
	private static final String dh = ",";
	private static final String mh = ":";
	/**
	 * @author debo.zhang
	 * @param args
	 */
	public static void main(String[] args) {
/*		Quercus quercus = new Quercus();
		Env testEnv = new Env(quercus);
		System.out.println("php_version:"+testEnv.getConstant("PHP_VERSION"));
		*/
//		Env env  = new PHP("classpath:/net/collegeman/phpinjava/php/production_config.inc.php").getEnv();
		
//		String config_file = "http://127.0.0.1:10086/development.config.php";
		String config_file = "classpath:/net/collegeman/phpinjava/php/gpdroid_api/development_config.inc.php";
		Env env  = new PHP(config_file).getEnv();
//		Env env  = new PHP("classpath:/net/collegeman/phpinjava/php/gpdroid_api/development_config.inc.php").getEnv();
		System.out.println("php_version:"+env.getConstant("PHP_VERSION"));
		//获取数组
		Map<StringValue, EnvVar> map = env.getEnv();
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("[");
		for(StringValue key:map.keySet()){
//			System.out.println("=====key:"+key+"=====");
			sBuffer.append("{").append(yh).append(key).append(yh).append(mh);
			EnvVar var =	map.get(key);
			
			if(var.getVar().isString()){
//				System.out.println("====value:"+var.toString()+"====");
				sBuffer.append(yh).append(var.toString()).append(yh).append(dh).append("}").append(dh);
				continue;
			}
			Iterator<Map.Entry<Value,Value>> iterator = var.getVar().getIterator(env);
			delegate(iterator,env,sBuffer);
			sBuffer.append("}").append(dh);
		}
		sBuffer.append("]");
		System.err.println(sBuffer.toString()
				.replace(",]", "]")
				.replace(",}", "}")
				.replace("[{[{", "[{")
				.replace("}]}]", "}]")
				.replace("},}", "}}")
				.replace("}],[{", "},{")
				);
	}
	
	/**
	 * @author debo.zhang
	 * @param iterator
	 * @param env
	 * @param sBuffer
	 */
	public static void delegate(Iterator<Map.Entry<Value,Value>> iterator,Env env, StringBuffer sBuffer){
		sBuffer.append("{");
		while(iterator.hasNext()){
			Map.Entry<Value,Value> vv = iterator.next();
			if(!vv.getValue().isArray()){
//				System.out.println(vv.getKey()+":"+vv.getValue());
				sBuffer.append(yh).append(vv.getKey().toString()).append(yh).append(mh).append(yh).append(ValueHandler(vv.getValue())).append(yh).append(dh);
			}else{
				if(vv.getKey().isNumeric()){
					/*
					 * array (
					 * 		array (
					 * 			
					 * 		)
					 * )
					 */
					sBuffer.append("[");
				}else if(vv.getKey().isString()) {
					/*
					 * array(
					 * 		'gpdroid' => array (
					 * 			
					 * 		)
					 * )
					 */
	//				System.out.println("==="+vv.getKey().toString()+"===");
					sBuffer.append(yh).append(vv.getKey().toString()).append(yh).append(mh).append("[");
				}else {
					System.err.println("=========");
				}
				
				delegate(vv.getValue().getIterator(env),env,sBuffer);
				sBuffer.append("]").append(dh);
			}
		}
		sBuffer.append("}").append(dh);
	}
	
	
	static String ValueHandler(Value value){
		if(value.isNull()){
			return "\"\"";
		}else if (value.isNumeric()) {
			return String.valueOf(value.toInt());
		}else if (value.isBoolean()) {
			return String.valueOf(value.toBoolean());
		}
		return value.toString();
	}
}
