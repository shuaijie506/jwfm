package com.dx.jwfm.framework.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;


/**
 * 河南辉煌科技股份有限公司 版权所有 2010
 * 创建人： 王涛
 * 创建时间： 2010-10-8 下午03:19:24
 * 功能描述：文件上传通用类
 * ==================================
 * 修改历史
 * 修改人        修改时间      修改位置（函数名）
 *
 * ==================================
 */
public class NetFileUtil {

	static Logger logger = Logger.getLogger(NetFileUtil.class);
	
	/**
	 * 开发人： 王涛
	 * 开发时间： 2010-11-8 上午10:31:58
	 * 功能描述：获取文件下载
	 * 方法的参数和返回值
	 * @param vcId
	 * @return
	 * String 
	 * ==================================
	 * 修改历史
	 * 修改人        修改时间      修改原因及内容
	 *
	 * ==================================
	 */
	public static String getFileUrl(String vcId) {
		return SystemContext.path+ SystemContext.getSysParam("uploadFileDir","/uploadfile")+"/" + vcId;
	}

	public static String getFilesHtml(String ids){
		return getFilesHtml(ids,null);
	}
	

	public static String getFilesHtml(String ids, String split) {
		if(FastUtil.isBlank(ids))
			return "";
		if (split == null)
			split = "<br>";
		StringBuffer url = new StringBuffer();
		List<FastPo> list = searchFilePo(ids);
		for (int i=0;i<list.size();i++) {
			FastPo po = list.get(i);
			if(i>0){
				url.append(split);
			}
			url.append("<a href='"+ getFileUrl(po.getString("VC_ID")) +"' target='_blank'>" + po.getString("VC_NAME")+
					"(" + FastUtil.formatFileSize(po.getLong("N_BYTES")) + ")</a>");
		}
		return url.toString();
	}
	
	public static List<FastPo> searchFilePo(String ids) {
		if(FastUtil.isBlank(ids)){
			return new ArrayList<FastPo>();
		}
		String sql = "select * from "+SystemContext.dbObjectPrefix+"T_FILE_UPLOAD where ";
		List<FastPo> list = null;
		DbHelper db = new DbHelper();
		FastPo po = FastPo.getPo(SystemContext.dbObjectPrefix+"T_FILE_UPLOAD");
		try {
			if(ids.indexOf(",")>0){
				list = db.executeSqlQuery(sql+"vc_id in ('" + ids.replaceAll(",", "','") + "')",po);
			}
			else{
				list = db.executeSqlQuery(sql+"vc_id=?",po,new Object[]{ids});
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return list==null?new ArrayList<FastPo>():list;
	}

	public static String getImgsHtml(String ids){
		if(FastUtil.isBlank(ids))
			return "";
		String split = "<br>";
		StringBuffer url = new StringBuffer();
		List<FastPo> list = searchFilePo(ids);
		for (int i=0;i<list.size();i++) {
			FastPo po = list.get(i);
			if(i>0){
				url.append(split);
			}
			url.append("<img src='"+ getFileUrl(po.getString("VC_ID")) +"' /><br/><a href='"+ getFileUrl(po.getString("VC_ID")) +
					"' target='_blank'>"  + po.getString("VC_NAME")+"(" + FastUtil.formatFileSize(po.getLong("N_BYTES")) + ")</a>");
		}
		return url.toString();
	}
	
	private static File getFileOnDisk(FastPo po){
		String baseDir = FastUtil.getRegVal("SYSTEM_WEB_FILE_DIR",SystemContext.getAppPath()+"/../uploadfile");
		String[] dirs = baseDir.split(";");
		String dir = null;
		for(int i=dirs.length-1;i>=0;i--){
			dir = dirs[i];
			File base = new File(dir);
			File _tmpFile = new File(new File(base, po.getString("VC_PATH")), po.getString("VC_MD5"));
			if(_tmpFile.exists()){
				return _tmpFile;
			}
		}
		return null;
	}
	public static File getFile(String id) throws FileNotFoundException{
		List<FastPo> list = searchFilePo(id);
		if(list.size()>0){
			FastPo po = list.get(0);
			return getFile(po);
		}
		return null;
	}
	public static File getFile(FastPo po) throws FileNotFoundException{
		if(po!=null){
			File f = getFileOnDisk(po);
			if(f!=null){
				return f;
			}
			String server = po.getString("VC_SERVER");
			if(FastUtil.isNotBlank(server)){
				String remoteUrl = FastUtil.getDictVal("框架-远程文件下载地址", server);
				remoteUrl += (remoteUrl.indexOf("?")>0?"&":"?")+po.getVcId();
				HashMap<String, String> cookie = new HashMap<String, String>();
				cookie.put("SSO_KEY", EncryptUtil.getSsoKey(null));
				InputStream in = null;
				try {//得到远程服务器的文件流后，将内容写入到本地文件中
					in = NetUtil.downloadFile(remoteUrl, cookie);
					saveFile(in,po);
					return getFileOnDisk(po);
				} catch (IOException e) {
					throw new FileNotFoundException("can't find file from remote server:"+remoteUrl);
				}
			}
		}
		return null;
	}
	
	public static String saveFile(File f,FastPo po) throws FileNotFoundException{
		if(po==null){
			po = FastPo.getPo(SystemContext.dbObjectPrefix+"T_FILE_UPLOAD");
			po.put("VC_NAME", f.getName());
		}
		else{
			po.setTableModelName(SystemContext.dbObjectPrefix+"T_FILE_UPLOAD");
		}
		if(!"true".equals(FastUtil.getRegVal("SYSTEM_NO_TRANS_BMP"))){//默认将bmp强制转换为jpg，节省空间，同时提高图片下载速度
			String fileName = FastUtil.nvl(po.getString("VC_NAME"),f.getName());
			if(fileName.toLowerCase().endsWith(".bmp")){
				f = transBmpImg(f);
			}
		}
		initFilePo(po);
		String md5 = po.getString("VC_MD5");
		md5 = md5!=null && md5.length()>0?md5:FastUtil.toMd5String(f);
		po.put("VC_MD5", md5);
		po.put("N_BYTES", f.length());
		try {//查询服务器上是否有相同MD5值和文件大小的文件，如果有，则不在服务器上重复保存
			String sql = "select * from "+SystemContext.dbObjectPrefix+"T_FILE_UPLOAD where vc_md5=? and n_bytes=?";
			DbHelper db = new DbHelper();
			List<FastPo> list = db.executeSqlQuery(sql,new Object[]{md5,f.length()});
			for(FastPo fp:list){
				File tmpFile = getFileOnDisk(fp);
				if(tmpFile!=null && tmpFile.exists() && tmpFile.length()==f.length()){
					po.put("VC_PATH", fp.getString("VC_PATH"));
					po.initDefaults();
					db.insertOrUpdatePo(po);
					return po.getVcId();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return saveFile(new FileInputStream(f), po,false);
	}

	public static String saveFile(InputStream in,FastPo po){
		return saveFile(in, po, true);
	}
	
	public static String saveFile(InputStream in,FastPo po,boolean checkMd5){
		BufferedOutputStream out = null;
		try {
			initFilePo(po);
			byte[] buff = new byte[8192];
			int len = 0;
			String baseDir = FastUtil.getRegVal("SYSTEM_WEB_FILE_DIR",SystemContext.getAppPath()+"/../uploadfile");
			String[] dirs = baseDir.split(";");
			String dir = dirs[dirs.length-1];
			String path = po.getString("VC_MODULE");
			if(!path.endsWith("/") && !path.endsWith("\\")){
				path += "/";
			}
			path += FastUtil.format(po.getDate("DT_UPLOAD"), "yyyy/MMdd");
			po.put("VC_PATH", path);
			File basePath = new File(dir,path);
			if(!basePath.exists()){
				basePath.mkdirs();
			}
			File tmpFile = new File(basePath,FastUtil.getUuid());
			out = new BufferedOutputStream(new FileOutputStream(tmpFile));
			while((len=in.read(buff))>=0){
				out.write(buff,0,len);
			}
			out.close();
			String md5 = po.getString("VC_MD5");
			md5 = md5!=null && md5.length()>0?md5:FastUtil.toMd5String(tmpFile);
			po.put("VC_MD5", md5);
			po.put("N_BYTES", tmpFile.length());
			DbHelper db = new DbHelper();
			try {//查询服务器上是否有相同MD5值和文件大小的文件，如果有，则不在服务器上重复保存
				if(checkMd5){
					String sql = "select * from "+SystemContext.dbObjectPrefix+"T_FILE_UPLOAD where vc_md5=? and n_bytes=?";
					List<FastPo> list = db.executeSqlQuery(sql,new Object[]{md5,tmpFile.length()});
					for(FastPo fp:list){
						File tmpFile2 = getFileOnDisk(fp);
						if(tmpFile2.exists() && tmpFile2.length()==tmpFile.length()){
							po.put("VC_PATH", fp.getString("VC_PATH"));
							return po.getVcId();
						}
					}
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
			finally{
				try {
					po.initDefaults();
					db.insertOrUpdatePo(po);
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			tmpFile.renameTo(new File(basePath,md5));//文件生成后直接重命名，避免多线程同时写入文件引起错误
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return po.getVcId();
	}
	

	private static void initFilePo(FastPo po) {
		po.put("VC_ID", FastUtil.nvl(po.getString("VC_ID"), FastUtil.getUuid()));
		po.put("VC_MODULE", FastUtil.nvl(po.getString("VC_MODULE"), "blankModule"));
		if(po.getString("VC_MODULE").startsWith("/") || po.getString("VC_MODULE").startsWith("\\")){
			po.put("VC_MODULE", po.getString("VC_MODULE").substring(1));
		}
		po.put("N_QUOTE", FastUtil.nvl(po.getString("N_QUOTE"), "1"));
		po.put("DT_UPLOAD", po.getDate("DT_UPLOAD")==null?new Date():po.getDate("DT_UPLOAD"));
	}


	private static File noImgFile;
	public static String getNoImgFileUrl(){
		return SystemContext.path+"/jwfm/images/no_img.jpg";
	}
	public static File getNoImgFile(){
		if(noImgFile==null){
			noImgFile = new File(SystemContext.getAppPath()+"/images/no_img.jpg");
		}
		return noImgFile;
	}
	
	public BufferedImage scaleImage(BufferedImage srcImg,int newWidth,int newHeight){
		if (newWidth<=0)//不指定宽度时，宽度放到一个较大范围
			newWidth = 999999999;
		if (newHeight<=0)//不指定高度时，高度放到一个较大范围
			newHeight = 999999999;
		int w = srcImg.getWidth();//图片原宽度
		int h = srcImg.getHeight();//图片原调度
		if(newWidth>=w && newHeight>=h){
			return srcImg;
		}
		int nw = 0, nh = 0;
		if (w > newWidth) {
			nw = newWidth;
			nh = Math.max(1,nw * h / w);
		}
		if (nh > newHeight) {
			nh = newHeight;
			nw = Math.max(1,nh * w / h);
		}
		nw = nw==0?w:nw;
		nh = nh==0?h:nh;
		/*
		Image i = bi.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		bi = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
		Graphics2D biContext = bi.createGraphics();
		biContext.drawImage(i, 0, 0, null);
		ImageIO.write(bi, "jpg", bs);
		*/
		AffineTransform transform = new AffineTransform();
		transform.setToScale((double) nw / w, (double) nh / h); // 设置缩放比例
		BufferedImage bid = new BufferedImage(nw,nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D gd2 = bid.createGraphics();
        gd2.drawImage(srcImg, transform,null);
        gd2.dispose();
        return bid;
	}

	 public static BufferedImage rotateImage(final BufferedImage bufferedimage,final int degree) {
       int w = bufferedimage.getWidth();
       int h = bufferedimage.getHeight();
       int type = bufferedimage.getColorModel().getTransparency();
       BufferedImage img;
       Graphics2D graphics2d;
       int x = w/2,y = h/2;
       if(degree/90%2==1){
    	   w = h;
    	   h = bufferedimage.getWidth();
    	   if(degree/90%4==1 && h>w){
        	   x = y = Math.min(x, y);
    	   }
    	   else{
        	   x = y = Math.max(x, y);
    	   }
       }
       (graphics2d = (img = new BufferedImage(w, h, type))
               .createGraphics()).setRenderingHint(
               RenderingHints.KEY_INTERPOLATION,
               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
       graphics2d.rotate(Math.toRadians(degree), x, y);
       graphics2d.drawImage(bufferedimage, 0, 0, null);
       graphics2d.dispose();
       return img;
   }
	
	private static File transBmpImg(File uploadFile){
		try {
			BufferedImage bi = ImageIO.read(new FileInputStream(uploadFile));
			File tmpFile = File.createTempFile("transImg", ".jpg");
			FileOutputStream out = new FileOutputStream(tmpFile);
			ImageIO.write(bi, "jpg", out);
			out.close();
			return tmpFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadFile;
	}
//	/**
//	 * 开发人： 王涛
//	 * 开发时间： 2010-11-8 上午10:34:39
//	 * 功能描述：图片文件下载，若本地存在要下载的图片文件，则直接下载；否则从远程同步到本地，然后下载
//	 * 方法的参数和返回值
//	 * @param response
//	 * @param fileId
//	 * @param width
//	 * @param height
//	 * @param angle 图片旋转角度 0不旋转 1顺时针转90度，2顺时针转180度，3顺时针转270度，其他值返回原图
//	 * void 
//	 * ==================================
//	 * 修改历史
//	 * 修改人        修改时间      修改原因及内容
//	 *
//	 * ==================================
//	 */
//	public static void getDownPicFile(HttpServletResponse response,
//			String fileId, int width, int height,String angle, boolean save) {
//		Connection con = null;
//		InputStream bis = null;
//		OutputStream bos = null;
//		Statement st = null;
//		ResultSet rs = null;
//		String fileName = null, childFolder = null;
//		long filesize = 0;
//		boolean b=false;
//		try {
//			String sql = "update pub_t_file_upload_log set vc_del$flag=0,n_down=n_down+1 where vc_id='"+fileId+"'";
//			DbUtil.executeSqlUpdate(sql);//将文件删除标记更新为0
//			con = SystemContext.getConnection();
//			String sqlStr = "SELECT VC_ID,VC_FILENAME,VC_CHILD$FOLDER,n_filesize FROM PUB_T_FILE_UPLOAD_LOG WHERE VC_ID ='"
//					+ fileId + "'";
//			st = con.createStatement();
//			Logger.getLogger(NetFileUtil.class).info(sqlStr);
//			rs = st.executeQuery(sqlStr);
//			
//			while (rs.next()) {
//				fileId = rs.getString(1);
//				fileName = rs.getString(2);
//				childFolder = rs.getString(3);
//				filesize = rs.getLong(4);
//				break;
//			}
//			if (childFolder == null)
//				childFolder = "oldfile/";
//			if (fileName == null)
//				fileName = fileId;
//			response.setCharacterEncoding("UTF-8");
//			//String tt=new String(fileName.getBytes("iso8859-1"),"GBK");
//			if(fileName.lastIndexOf(".")>0 && ",jpg,gif,jpeg,png,bmp".indexOf(","+fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase())>=0){
//				response.setContentType("image/"+fileName.substring(fileName.lastIndexOf(".")+1).toUpperCase()); // MIME type for pdf doc
//			}
//			else{
//				response.setContentType("image/jpeg"); // MIME type for pdf doc
//			}
//			response.setHeader("Content-disposition", "attachment; filename=\""
//					+ new String(fileName.getBytes("GBK"),"ISO8859-1") + "\"");
//			bos = new BufferedOutputStream(response.getOutputStream());
//			int degree = 0;
//			try {
//				degree = Integer.parseInt(angle)%4;
//			} catch (NumberFormatException e1) {
//			}
//			if (width<=0&&height<=0 && Math.abs(degree-2)>1) {// 下载文件图片
//				bis = NetFileUtil.getInputStream(childFolder, fileId);
//				if(bis!=null && filesize>5*1024*1024)//3M以上的文件，指定文件大小，小文件不指定
//					response.setHeader("Content-Length",""+filesize);
//			} else {// 打开图片
//				bis = NetFileUtil.getImage(childFolder, fileId, width,height,angle,save);
//			}
//			if(bis==null){
//				String fname = fileName.toLowerCase().substring(Math.max(0, fileName.length()-4));
//				if(".jpg,.png,.gif,.bmp".indexOf(fname)>=0){
//					bis = new FileInputStream(getNoImgFile());
//				}
//				else{
//					throw new FileNotFoundException(fileName);
//				}
//			}
//			byte[] buff = new byte[2048];
//			int bytesRead;
//			bytesRead = bis.read(buff, 0, buff.length);
//			while (bytesRead >= 0) {
//				bos.write(buff, 0, bytesRead);
//				bytesRead = bis.read(buff, 0, buff.length);
//			}
//			sqlStr = "UPDATE PUB_T_FILE_UPLOAD_LOG SET DT_LAST_DOWN=SYSDATE,N_DOWN=N_DOWN+1 WHERE VC_ID ='"
//				+ fileId + "'";
//			Logger.getLogger(NetFileUtil.class).info(sqlStr);
//			st.executeUpdate(sqlStr);
//		} catch (FileNotFoundException e) {
//			b=true;
//			Logger.getLogger(NetFileUtil.class).error("读取"+childFolder+fileName+"文件不存在！",e);
//			//e.printStackTrace();
//		} catch(SocketException e)
//		{
//			Logger.getLogger(NetFileUtil.class).error("读取"+childFolder+fileName+"文件时，出现ClientAbortException:  java.net.SocketException: Connection reset by peer: socket write error",e);
//		}catch (IOException e) {
//			b=true;
//			Logger.getLogger(NetFileUtil.class).error("读取"+childFolder+fileName+"文件时，出现输入输出错误！",e);
//			//e.printStackTrace();
//		}
//		catch (SQLException e) {
//			b=true;
//			Logger.getLogger(NetFileUtil.class).error("读取"+childFolder+fileName+"文件时，数据库发生链接异常",e);
//			//e.printStackTrace();
//		} catch (AppException e) {
//			b=true;
//			//e.printStackTrace();
//		} finally {
//			try {
//				if(b)
//				{
//					bos.write("<script>alert('要下载的文件找不到，请联系管理员！');window.close();</script>".getBytes());
//				}
//				if(bis!=null)
//				{
//					bis.close();
//				}
//				if(bos!=null)
//				{
//					bos.flush();
//					bos.close();
//				}
//			} catch (IOException e) {
//			}
//			try {
//				rs.close();
//				st.close();
//				con.close();
//			} catch (SQLException e) {
//				//e.printStackTrace();
//			}
//
//		}
//	}
	public static void downloadFile(String fileId) {
		InputStream bis = null;
		OutputStream bos = null;
		String fileName = null, childFolder = null;
		HttpServletRequest request = RequestContext.getRequest();
		HttpServletResponse response = RequestContext.getResponse();
		if(request==null || response==null){//如果没有response对象，则不执行下载操作
			return;
		}
		try {
			response.setCharacterEncoding("UTF-8");
			List<FastPo> list = searchFilePo(fileId);
			if(list.isEmpty()){
				throw new FileNotFoundException();
			}
			DbHelper db = new DbHelper();
			FastPo po = list.get(0);
			bos = new BufferedOutputStream(response.getOutputStream());
			if(po!=null){
				fileId = po.getVcId();
				fileName = po.getString("VC_NAME");
				childFolder = po.getString("VC_PATH");
				po.setPropt("N_DOWNLOAD", po.getInteger("N_DOWNLOAD")==null?1:po.getInteger("N_DOWNLOAD")+1);
				po.setPropt("N_DEL", 0);
				try {
					db.updatePo(po);
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if (childFolder == null)
				childFolder = "oldfile/";
			if (fileName == null)
				fileName = fileId;
			//String tt=new String(fileName.getBytes("iso8859-1"),"GBK");
			String fnamelow = fileName.toLowerCase();
			int pos = fnamelow.lastIndexOf(".");
			String fnameext = pos>0?fnamelow.substring(pos+1):"";
			if(",jpg,jpeg,png,bmp,gif,tif,svg,".indexOf(","+fnameext+",")>=0){
				response.setContentType("image/"+fnameext); // MIME type for image file
			}
			else{
				response.setContentType("application/zip"); // MIME type for other file
			}
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ new String(fileName.getBytes("GBK"),"ISO8859-1") + "\"");
			File f = getFile(po);
			if(f==null){
				throw new FileNotFoundException();
			}
			int rangeStart = 0;//要下载的字节开始序号
			int rangeEnd = -1;//要下载的字节结束序号
			//分块下载
			response.setHeader("Content-Length", String.valueOf(f.length()));
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("ETag", fileId);
			String range = request.getHeader("RANGE");
			if(range!=null){
				range = range.trim().toLowerCase();
				if(range.startsWith("bytes=")){
					String[] ary = range.substring(6).split("-");
					try{rangeStart = Integer.parseInt(ary[0]);}catch(Exception e){} 
					try{rangeEnd = Integer.parseInt(ary[1]); }catch(Exception e){}
				}
			}
			if(rangeStart!=rangeEnd && (rangeStart>0 || rangeEnd>rangeStart)){//按块下载
				response.setHeader("Content-Range", "bytes "+rangeStart+"-"+String.valueOf(rangeEnd>rangeStart?rangeEnd:f.length()-1)+"/"+f.length());
			}
			bis = new FileInputStream(f);
			byte[] buff = new byte[8129];
			int bytesRead,allRead=0;
			if(rangeStart>0){//跳过开始区块
				bis.skip(rangeStart);
			}
			if(rangeEnd>rangeStart && rangeEnd<f.length()-1){//下载中间区块
				allRead = rangeStart;
				while (-1 != (bytesRead = (bis.read(buff, 0, buff.length)))){
					bos.write(buff, 0, Math.max(rangeEnd-allRead,bytesRead));//max取值防止超长
					allRead += bytesRead;
				}
			}
			else{//下载之后全部内容
				while (-1 != (bytesRead = (bis.read(buff, 0, buff.length)))){
					bos.write(buff, 0, bytesRead);
				}
			}
			
			
		} catch (FileNotFoundException e) {
			logger.error("读取"+childFolder+fileName+"文件不存在！");
			logger.error(e.getMessage(),e);
			try {
				bos.write("<script>alert('要下载的文件找不到，请联系管理员！');window.close();</script>".getBytes());
			} catch (IOException e1) {
				logger.error(e1.getMessage(),e1);
			}
		} catch(SocketException e){
			logger.error("文件下载因网络问题中断！");
		}catch (IOException e) {
			logger.error("读取"+childFolder+fileName+"文件时，出现输入输出错误！",e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
//
//	public static String getFileName(String vcFileid) {
//		String sql = "select VC_FILENAME from PUB_T_FILE_UPLOAD_LOG where vc_id='"+vcFileid+"'";
//		return DbUtil.getFirstStringSqlQuery(sql);
//	}
	/**
	 * @author ： 王涛
	 * @since： Oct 6, 2011 11:15:10 AM
	 * 功能描述：根据文件ID删除文件
	 * 方法的参数和返回值
	 * @param fileIds
	 * void 
	 * ==================================
	 * 修改历史
	 * 修改人        修改时间      修改原因及内容
	 *
	 * ==================================
	 */
	public static void deleteFilePhysical(String fileIds){
		List<FastPo> list = searchFilePo(fileIds);
        if(list.isEmpty()){
            return;
        }
		try {
			DbHelper db = new DbHelper();
			String sql = "select count(*) from "+SystemContext.dbObjectPrefix+"T_FILE_UPLOAD where n_del=0 and vc_md5=? and n_bytes=? and vc_id!=?";
			for(FastPo po:list){
				if(db.getFirstIntSqlQuery(sql, new Object[]{po.getString("VC_MD5"),po.getLong("N_BYTES"),po.getString("VC_ID")})>0){
					continue;//如果还有其他文件指向此文件实际路径，则不物理删除
				}
				File f = getFileOnDisk(po);
				if(f!=null && f.exists() && f.isFile()){
					logger.info("delete file :"+f.getCanonicalPath());
					f.delete();
				}
				po.setPropt("N_DEL", 1);
				po.setTableModelName(SystemContext.dbObjectPrefix+"T_FILE_UPLOAD");
				new DbHelper().updatePo(po);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}



}
