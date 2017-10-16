package com.zxz.dao;

import org.apache.log4j.Logger;

import com.zxz.domain.Vedio;

public class VedioDao extends BaseDao<Vedio>{
	
	private static Logger logger = Logger.getLogger(VedioDao.class);
	static VedioDao vedio;
	static int id=0;
	
	public VedioDao() {
	}
	
	public static VedioDao getInstance(){
		if(vedio!=null){
			return vedio;
		}else{
			synchronized (VedioDao.class) {
				vedio = new VedioDao();
				return vedio;
			}
		}
	}
	
	/**淇濆瓨褰曞儚
	 * @param vedio
	 * @return
	 */
	public int saveVedio(Vedio vedio){
		return super.insert("Vedio.save", vedio);
	}


	public static void main(String[] args) {
		Vedio vedio = new Vedio();
		VedioDao instance = VedioDao.getInstance();
		vedio.setRecord("12");
		instance.saveVedio(vedio);
		System.out.println(vedio.getId());
	}
	
}
