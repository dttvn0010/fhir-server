package vn.moh.fhir.utils;

public class Constants {

    public static interface IdentifierSystem{
    	final public static String MA_BENH_NHAN = "https://www.moh.gov.vn/identifier/MaBenhNhan";
        final public static String DINH_DANH_YTE = "https://www.moh.gov.vn/identifier/DinhDanhBenhNhan";
        final public static String CMND = "https://www.moh.gov.vn/identifier/CMND";
        final public static String SO_BENH_AN = "https://www.moh.gov.vn/identifier/SoBenhAn";
        final public static String SO_THE_BHYT = "https://wwwmoh.gov.vn/identifier/SoTheBHYT";
        final public static String SO_THE_KHUYET_TAT = "https://wwwmoh.gov.vn/identifier/SoTheKhuyetTat";
        final public static String CO_SO_YTE = "https://wwwmoh.gov.vn/identifier/CoSoYTe";
        final public static String CHUNG_CHI_HANH_NGHE = "https://www.moh.gov.vn/identifier/ChungChiHanhNghe";
        final public static String TBYT = "https://wwwmoh.gov.vn/identifier/TBYT";
        final public static String INSURER = "https://wwwmoh.gov.vn/identifier/Insurer";
        final public static String MA_PHIEU_PTTT = "https://wwwmoh.gov.vn/identifier/MaPhieuPTTT";
    }
   
    public static interface ExtensionURL {
    	// Patient    	
        final public static String PATIENT_ETHNIC = "https://www.moh.gov.vn/extension/Patient/Ethnic";
        final public static String PATIENT_RELIGION = "https://www.moh.gov.vn/extension/Patient/Relgion";
        final public static String PATIENT_EDUCATION = "https://www.moh.gov.vn/extension/Patient/Education";
        final public static String PATIENT_JOB = "https://www.moh.gov.vn/extension/Patient/Job";
        final public static String PATIENT_NATIONALITY = "https://www.moh.gov.vn/extension/Patient/Nationality";        
        
        // Address
        final public static String ADDRESS_WARD = "https://www.moh.gov.vn/extension/Address/Ward";
        final public static String ADDRESS_DISTRICT = "https://www.moh.gov.vn/extension/Address/District";
        final public static String ADDRESS_CITY = "https://www.moh.gov.vn/extension/Address/City";        
        
    }
    
}
