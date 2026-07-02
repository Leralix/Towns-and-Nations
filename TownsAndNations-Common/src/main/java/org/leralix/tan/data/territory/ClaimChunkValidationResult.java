package org.leralix.tan.data.territory;

import org.leralix.tan.lang.FilledLang;


public class ClaimChunkValidationResult {

    private final boolean success;
    private final FilledLang errorMessage;

    private ClaimChunkValidationResult(boolean success, FilledLang errorMessage){
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ClaimChunkValidationResult success(){
        return new ClaimChunkValidationResult(true, null);
    }

    public static ClaimChunkValidationResult failure(FilledLang errorMessage){
        return new ClaimChunkValidationResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public FilledLang getErrorMessage(){
        return errorMessage;
    }
}
