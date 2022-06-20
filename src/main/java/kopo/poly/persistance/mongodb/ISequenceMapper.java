package kopo.poly.persistance.mongodb;

import kopo.poly.dto.SequenceDTO;

public interface ISequenceMapper {

    /**
     * 원하는 컬랙션의 시퀸스 값 출력
     *
     * @param colNm 컬렉션 이름
     * @return 시퀸스 값
     */
    SequenceDTO getSequence(String colNm) throws Exception;

}
