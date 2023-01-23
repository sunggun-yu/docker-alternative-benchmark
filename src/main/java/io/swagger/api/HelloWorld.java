package io.swagger.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

//@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/user/v1/")

public class HelloWorld {

	// Health URI Pattern for /user//v1/health
	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public ResponseEntity<String> getMsg() {
		return new ResponseEntity<>("live", HttpStatus.OK);
	}

	// GET Hi URI Pattern for /user/v1/hi?fname?<VALUE1>
	@ApiResponses(value = {

			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@RequestMapping(value = "/hi", method = RequestMethod.GET)
	public ResponseEntity<String> getMsg(
			@Parameter(name = "fname", description = "First Name", required = true) @RequestParam(value = "fname") String fname) {
		// trimming the string
		// fname = fname.replaceAll("[^a-zA-Z0-9]", "");
		// fname = getCleanProperty(fname);

		System.out.println("----> !" + fname);
		try {
			if (fname == null || fname.trim().isEmpty()) {
				System.out.println("String is empty");
			}
			// If string has @ character print 500 INTERNAL_SERVER_ERROR error
			Pattern p_email = Pattern.compile("\\@");
			Matcher m_email = p_email.matcher(fname);
			// boolean b = m.matches();
			boolean bemail = m_email.find();
			if (bemail) {
				System.out.println("There is a @ character in my string ");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			// If string has any special character print 400 Bad request Error

			Pattern p = Pattern.compile("[^A-Za-z0-9]");
			Matcher m = p.matcher(fname);
			// boolean b = m.matches();
			boolean b = m.find();
			if (b) {
				System.out.println("There is a special character in my string ");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			// HTTP Success 200 Messsage
			fname = fname.replaceAll("[^a-zA-Z0-9]", "");
			fname = getCleanProperty(fname);
			String HttPMethod = "GET";
			String HttpMethodStr = "Http Method " + HttPMethod + " is Successfull!";
			String output = String.join("", "Hi ", fname, "!", "\n", HttpMethodStr);
			return new ResponseEntity<>(output, HttpStatus.OK);
			// catch(Exception e){}
		} catch (Exception e) {
			System.out.println("All Exceptions:");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			// System.out.println("Finally Done");
		}
	}

	// POST Hi URI Pattern for /user//v1/{hi}?fname?<VALUE1>

	@PostMapping(value = "/{hi}")
	public ResponseEntity<String> test(
			@PathVariable(value = "hi") String PostString) {

		System.out.println("----> !" + PostString);
		try {
			if (PostString == null || PostString.trim().isEmpty()) {
				System.out.println("String is empty");
			}
			// If string has @ character print 500 INTERNAL_SERVER_ERROR error
			Pattern p_email = Pattern.compile("\\@");
			Matcher m_email = p_email.matcher(PostString);
			// boolean b = m.matches();
			boolean bemail = m_email.find();
			if (bemail) {
				System.out.println("There is a @ character in my string ");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			// If string has any special character print 400 Bad request Error

			Pattern p = Pattern.compile("[^A-Za-z0-9]");
			Matcher m = p.matcher(PostString);
			// boolean b = m.matches();
			boolean b = m.find();
			if (b) {
				System.out.println("There is a special character in my string ");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			// HTTP Success 200 Messsage
			PostString = PostString.replaceAll("[^a-zA-Z0-9]", "");
			PostString = getCleanProperty(PostString);
			String HttPMethod = "POST";
			String HttpMethodStr = "Http Method " + HttPMethod + " is Successfull!";
			String output = String.join("", "Hi ", PostString, "!", "\n", " Post Method Output ", HttpMethodStr);

			return new ResponseEntity<>(output, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("All Exceptions:");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			System.out.println("Finally Done");
		}
	}

	// Hello URI Pattern for /user//v1/hello?fname=<VALUE1>?lname=<VALUE2>
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public ResponseEntity<String> getMsg(
			@Parameter(name = "fname", description = "First Name", required = true) @RequestParam(value = "fname") String fname,
			@Parameter(name = "lname", description = "Last Name", required = true) @RequestParam(value = "lname") String lname) {
		// trimming the string
		fname = fname.replaceAll("[^a-zA-Z0-9]", "");
		fname = getCleanProperty(fname);

		lname = lname.replaceAll("[^a-zA-Z0-9]", "");
		lname = getCleanProperty(lname);

		return new ResponseEntity<>("Hello " + fname + "  " + lname + "!! Have a nice day!", HttpStatus.OK);
	}

	private String getCleanProperty(String property) {
		if (Objects.isNull(property)) {
			return StringUtils.EMPTY;
		}
		property = property.replaceAll("[\r\n]+", " ").replace("'", " ").replace("\\", " ").replace("\t", " ")
				.replace("\"", " ");
		return property;
	}
}
