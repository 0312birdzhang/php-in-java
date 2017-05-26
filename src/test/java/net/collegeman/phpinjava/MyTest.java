package net.collegeman.phpinjava;

import java.util.Iterator;
import java.util.Map;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;

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
//		Env env  = new PHP("classpath:/net/collegeman/phpinjava/php/production_config.inc.php").getEnv();
		Env env  = new PHP("classpath:/net/collegeman/phpinjava/php/development.config.php").getEnv();
//		Env env  = new PHP("classpath:/net/collegeman/phpinjava/php/gpdroid_api/development_config.inc.php").getEnv();
		System.out.println("php_version:"+env.getConstant("PHP_VERSION"));
		System.exit(0);
		//获取数组
		Map<String, Var> map = env.getEnv();
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("[");
		for(String key:map.keySet()){
//			System.out.println("=====key:"+key+"=====");
			sBuffer.append("{").append(yh).append(key).append(yh).append(mh);
			Var var =	map.get(key);
			if(var.isString()){
//				System.out.println("====value:"+var.toString()+"====");
				sBuffer.append(yh).append(var.toString()).append(yh).append(dh).append("}").append(dh);
				continue;
			}
			Iterator<Map.Entry<Value,Value>> iterator = var.getIterator(env);
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
