package org.kau.kkoolbeeServer.domain.payment.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ComponentScan
@RestController
public class PaymentController {

    @Value("${iamport.key}")
    private String restApiKey;
    @Value("${iamport.secret}")
    private String restApiSecret;
    private IamportClient iamportClient;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }


   /* @PostMapping("/verifyIamport/{imp_uid}")
    public ResponseEntity<Map<String, String>> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(imp_uid);
        Map<String, String> result = new HashMap<>();

        if (response.getResponse().getStatus().equals("paid")) {
            result.put("status", "paid");
        } else {
            result.put("status", "failed");
        }

        return ResponseEntity.ok(result);




    }*/

    /*@PostMapping("/verifyIamport/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
        return iamportClient.paymentByImpUid(imp_uid);
    }*/
    @PostMapping("/verifyIamport/{imp_uid}")
    public ResponseEntity<?> verifyPayment(@PathVariable("imp_uid") String impUid) {
        try {
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
            if (response.getResponse() != null) {
// 결제 검증 로직 추가 (예: 결제 금액 검증, 결제자 정보 검증 등)
                return ResponseEntity.ok(response.getResponse());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ErrorType.NOT_FOUND_PAYMENT));
            }
        } catch (IamportResponseException e) {
// 아임포트 서버로부터 오류 응답을 받은 경우
            return ResponseEntity.status(e.getHttpStatusCode()).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,e.getMessage()));
        } catch (IOException e) {
// 네트워크 오류 등의 IOException 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR,"서버 오류가 발생했습니다."));
        }
    }


}
