package com.notification;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cparams;
import com.log.SceneLogUtil;

public class MailUtil {
	private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);
	private MimeMessage mimeMsg; // MIME邮件对象
	private Session session; // 邮件会话对象
	private Properties props; // 系统属性
	private boolean needAuth = false; // smtp是否需要认证
	// smtp认证用户名和密码
	private String username;
	private String password;
	private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象
	SceneLogUtil oplog;

	/**
	 * Constructor
	 * 
	 * @param smtp
	 *            邮件发送服务器
	 */
	public MailUtil(String smtp, SceneLogUtil oplog) {
		this.oplog = oplog;
		setSmtpHost(smtp);
		createMimeMessage();
	}

	/**
	 * 设置邮件发送服务器
	 * 
	 * @param hostName
	 *            String
	 */
	public void setSmtpHost(String hostName) {
		logger.info("set email system:mail.smtp.host = " + hostName);
		if (props == null)
			props = System.getProperties(); // 获得系统属性对象
		props.put("mail.smtp.host", hostName); // 设置SMTP主机
	}

	/**
	 * 创建MIME邮件对象
	 * 
	 * @return
	 */
	public boolean createMimeMessage() {
		try {
			logger.info("prepare to get email session  !");
			session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
		} catch (Exception e) {
			logger.error("create email session failed!", e);
			return false;
		}

		logger.info("prepare to get MIME!");
		try {
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			mp = new MimeMultipart();

			return true;
		} catch (Exception e) {
			logger.error("create MIME failed!", e);
			return false;
		}
	}

	/**
	 * 设置SMTP是否需要验证
	 * 
	 * @param need
	 */
	public void setNeedAuth(boolean need) {
		logger.info("set smtp authentication: mail.smtp.auth = " + need);
		if (props == null)
			props = System.getProperties();
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}

	/**
	 * 设置用户名和密码
	 * 
	 * @param name
	 * @param pass
	 */
	public void setNamePass(String name, String pass) {
		username = name;
		password = pass;
	}

	/**
	 * 设置邮件主题
	 * 
	 * @param mailSubject
	 * @return
	 */
	public boolean setSubject(String mailSubject) {
		logger.info("set email Subject!");
		try {
			mimeMsg.setSubject(mailSubject);
			return true;
		} catch (Exception e) {
			logger.error("set email Subject failed!", e);
			return false;
		}
	}

	/**
	 * 设置邮件正文
	 * 
	 * @param mailBody
	 *            String
	 */
	public boolean setBody(String mailBody) {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setContent("" + mailBody, "text/html;charset=GBK");
			mp.addBodyPart(bp);

			return true;
		} catch (Exception e) {
			logger.error("set email text failed!", e);
			return false;
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param filename
	 *            String
	 */
	public boolean addFileAffix(String filename) {
		logger.info("add email attachment:" + filename);
		if (filename == null)
			return true;
		File file = new File(filename);
		if (file.exists()) {
			try {
				BodyPart bp = new MimeBodyPart();
				FileDataSource fileds = new FileDataSource(filename);
				bp.setDataHandler(new DataHandler(fileds));
				bp.setFileName(MimeUtility.encodeText(fileds.getName()).replaceAll("\r", "").replaceAll("\n", "")); // 解决中文名称乱码
				mp.addBodyPart(bp);
				return true;
			} catch (Exception e) {
				logger.error("add email attachment: " + filename + " failed!", e);
				return false;
			}
		} else {
			logger.warn("email attachment=" + filename + " doesn't exist!");
			return false;
		}
	}

	/**
	 * 设置发信人
	 * 
	 * @param from
	 *            String
	 */
	public boolean setFrom(String from) {
		logger.info("set email from!");
		try {
			mimeMsg.setFrom(new InternetAddress(from)); // 设置发信人
			return true;
		} catch (Exception e) {
			logger.error("set email from: " + from + " failed!", e);
			return false;
		}
	}

	/**
	 * 设置收信人
	 * 
	 * @param to
	 *            String
	 */
	public boolean setTo(String to) {
		if (to == null)
			return false;
		try {
			to = to.replace(";", ",");
			mimeMsg.setRecipients(Message.RecipientType.TO, (Address[]) InternetAddress.parse(to));
			return true;
		} catch (Exception e) {
			logger.error("set email sendto: " + to + " failed!", e);
			return false;
		}
	}

	/**
	 * 设置抄送人
	 * 
	 * @param copyto
	 *            String
	 */
	public boolean setCopyTo(String copyto) {
		if (copyto == null)
			return false;
		try {
			copyto = copyto.replace(";", ",");
			mimeMsg.setRecipients(Message.RecipientType.CC, (Address[]) InternetAddress.parse(copyto));
			return true;
		} catch (Exception e) {
			logger.error("set email cc to: " + copyto + " failed!", e);
			return false;
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param hascc
	 * @return
	 */
	public boolean sendOut(boolean hascc) {
		Transport transport = null;
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			logger.info("email sending....");
			Session mailSession = Session.getInstance(props, null);
			transport = mailSession.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username, password);
			for (Address address : mimeMsg.getAllRecipients()) {
				try {
					transport.sendMessage(mimeMsg, (Address[]) InternetAddress.parse(address.toString()));
				} catch (SendFailedException e) {
					// TODO: handle exception
					logger.info("invalid address:" + address.toString());
					oplog.logTask("无效的邮件地址:" + address.toString());
				}
			}
			// transport.sendMessage(mimeMsg,mimeMsg.getAllRecipients());
			// try {
			// transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.TO));
			// }catch (SendFailedException e) {//群发邮件时如果有无效的不存在或者禁用的地址邮件会发送失败，需要去除无用地址重新发送
			// // TODO: handle exception
			// Address[] invalid = e.getInvalidAddresses();
			// if(invalid!=null){
			// for(Address address:invalid) logger.info("to invalid
			// address:"+address.toString());
			// }
			// Address[] validAddresses = e.getValidUnsentAddresses();
			// if(validAddresses!=null){
			// try {
			// mimeMsg.setRecipients(Message.RecipientType.TO,validAddresses);
			// sendFailOut(true);
			// } catch (MessagingException e1) {
			// // TODO Auto-generated catch block
			// logger.error("EXCEPITON",e1);
			// }
			//
			// }
			// }
			// try {
			// if(hascc)transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.CC));
			// }catch (SendFailedException e) {//群发邮件时如果有无效的不存在或者禁用的地址邮件会发送失败，需要去除无用地址重新发送
			// // TODO: handle exception
			// Address[] invalid = e.getInvalidAddresses();
			// if(invalid!=null){
			// for(Address address:invalid) logger.info("cc invalid
			// address:"+address.toString());
			// }
			// Address[] validAddresses = e.getValidUnsentAddresses();
			// if(validAddresses!=null){
			// try {
			// mimeMsg.setRecipients(Message.RecipientType.CC,validAddresses);
			// sendFailOut(false);
			// } catch (MessagingException e1) {
			// // TODO Auto-generated catch block
			// logger.error("EXCEPITON",e1);
			// }
			//
			// }
			// }
			logger.info("send email finished!");
			return true;
		} catch (Exception e) {
			logger.error("send email failed!", e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					logger.error("EXCEPITON", e);
				}
			}
		}
		return false;
	}

	/**
	 * 重新发送邮件
	 * 
	 * @param hascc
	 * @return
	 */
	public boolean sendFailOut(boolean flag) {
		Transport transport = null;
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			logger.info("email resending....");
			Session mailSession = Session.getInstance(props, null);
			transport = mailSession.getTransport(Cparams.smtp);
			transport.connect((String) props.get("mail.smtp.host"), username, password);
			if (flag) {
				transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
			} else {
				transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.CC));
			}
			logger.info("send email successful!");
			return true;
		} catch (Exception e) {
			logger.error("send email failed!", e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					logger.error("EXCEPITON", e);
				}
			}
		}
		return false;
	}

	/**
	 * 发送邮件
	 * 
	 * @param smtp
	 *            smtp服务器
	 * @param from
	 *            发信人
	 * @param to
	 *            收信人
	 * @param copyto
	 *            抄送
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param username
	 *            发信人姓名
	 * @param password
	 *            发信人密码
	 * @param args
	 *            附件路径
	 * @return
	 */
	public boolean SendMails(String smtp, String from, String to, String copyto, String subject, String content,
			String username, String password, String... args) {
		setNeedAuth(true); // 需要验证
		if (!setSubject(subject)) {
			oplog.logTask("设置邮件标题出错!");
			return false;
		}
		if (!setBody(content)) {
			oplog.logTask("设置邮件正文出错!");
			return false;
		}
		if (!setTo(to)) {
			oplog.logTask("设置收件人出错!");
			return false;
		}
		if (!setCopyTo(copyto)) {
			oplog.logTask("设置抄送人出错!");
			return false;
		}
		if (!setFrom(from)) {
			oplog.logTask("设置发件人出错!");
			return false;
		}
		for (String filepath : args) {
			if (!addFileAffix(filepath)) {
				oplog.logTask("添加附件出错:" + filepath);
				return false;
			}
		}
		setNamePass(username, password);
		if (!sendOut(copyto.equals("") ? false : true))
			return false;
		return true;
	}

}
