create or replace PROCEDURE WWW_GET(url VARCHAR2)
IS
    request UTL_HTTP.REQ;
    response UTL_HTTP.RESP;
    n NUMBER;
    buff VARCHAR2(4000);
    clob_buff CLOB;
BEGIN
    UTL_HTTP.SET_RESPONSE_ERROR_CHECK(FALSE);
    request := UTL_HTTP.BEGIN_REQUEST(url, 'GET');
    UTL_HTTP.SET_HEADER(request, 'User-Agent', 'Mozilla/4.0');
    response := UTL_HTTP.GET_RESPONSE(request);
    DBMS_OUTPUT.PUT_LINE('HTTP response status code: ' || response.status_code);

    IF response.status_code = 200 THEN
        BEGIN
            clob_buff := EMPTY_CLOB;
            LOOP
                UTL_HTTP.READ_TEXT(response, buff, LENGTH(buff));
		clob_buff := clob_buff || buff;
            END LOOP;
	    UTL_HTTP.END_RESPONSE(response);
	EXCEPTION
	    WHEN UTL_HTTP.END_OF_BODY THEN
                UTL_HTTP.END_RESPONSE(response);
	    WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE(SQLERRM);
                DBMS_OUTPUT.PUT_LINE(DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
                UTL_HTTP.END_RESPONSE(response);
        END;
    ELSE
        DBMS_OUTPUT.PUT_LINE('ERROR');
        UTL_HTTP.END_RESPONSE(response);
    END IF;
  
END;