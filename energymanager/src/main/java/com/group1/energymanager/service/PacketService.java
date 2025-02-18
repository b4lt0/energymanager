package com.group1.energymanager.service;

import com.group1.energymanager.DTOs.PacketDTO;
import com.group1.energymanager.model.Packet;
import com.group1.energymanager.repo.PacketRepository;
import com.group1.energymanager.repo.UserRepository;
import com.group1.energymanager.request.PacketRequest;
import com.group1.energymanager.response.BaseResponse;
import com.group1.energymanager.response.ListPacketResponse;
import com.group1.energymanager.response.PacketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PacketService {

    private final PacketRepository packetRepository;
    private final UserRepository userRepository;

    @Autowired
    public PacketService(PacketRepository packetRepository, UserRepository userRepository) {
        this.packetRepository = packetRepository;
        this.userRepository = userRepository;
    }

    public PacketResponse addPacket(PacketRequest packetRequest){
        PacketResponse resp=new PacketResponse();
        Packet packet = new Packet();

        // packet.setId("PAAAA0000"); //non va fatto, viene generato automaticamente
        //*prenderlo dal campo del packet request
        packet.setUserId(userRepository.findById(packetRequest.getUserId())
                .orElseThrow(()->new RuntimeException("UserNotFoundException")));
        packet.setDescription(packetRequest.getDescription());
        packet.setPrice(packetRequest.getPrice());
        packet.setQuantity(packetRequest.getQuantity());
        packet.setType(packetRequest.getType());

        packetRepository.save(packet);

        //popolo la response da riportare al controller
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Packet "+packet.getId()+" successfully created!");
        //resp.getPacket().setId(packet.getId()); //sets just the id
        resp.setPacket(packet.toDTO());
        resp.setResult(result);
        return resp;
    }

    public PacketResponse updatePacket(PacketRequest packetRequest){
        PacketResponse resp=new PacketResponse();

        //inserisco a db
        //Packet packet = new Packet(); //[PROVA] il frontend mi riempie gli altri campi che non sono stati modificati
        Packet packet = packetRepository.findById(packetRequest.getId())
                .orElseThrow(()->new RuntimeException("PacketNotFoundException"));
        //packet.setId(packetRequest.getId()); no perchè non voglio aggiornare l'id
        packet.setUserId(userRepository.findById(packetRequest.getUserId())
                .orElseThrow(()->new RuntimeException("UserNotFoundException"))); //da modificare con una vera eccezione
        packet.setDescription(packetRequest.getDescription());
        packet.setPrice(packetRequest.getPrice());
        packet.setQuantity(packetRequest.getQuantity());
        packet.setType(packetRequest.getType());

        packetRepository.save(packet); //salvo il nuovo (save funziona anche da 'update' in jpa)

        //popolo la response da riportare al controller
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Packet "+packet.getId()+" successfully updated!");
        //resp.getPacket().setId(packet.getId());
        resp.setPacket(packet.toDTO());
        resp.setResult(result);
        return resp;
    }

    public ListPacketResponse getAllPackets(){
        ListPacketResponse resp = new ListPacketResponse();
        //recupero pacchetti dal db
        List<Packet> packets = packetRepository.findAll();
        //creo lista di packetDTO
        List<PacketDTO> listPacketDTO = new ArrayList<>();
        PacketDTO tmp = new PacketDTO();
        for(Packet p : packets) {
            tmp.setId(p.getId());
            tmp.setUserId(p.getUserId());
            tmp.setDescription(p.getDescription());
            tmp.setQuantity(p.getQuantity());
            tmp.setPrice(p.getPrice());
            tmp.setType(p.getType());
            listPacketDTO.add(tmp);
        }
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Found List of Packets!");
        resp.setListPacketDTO(listPacketDTO);
        resp.setResult(result);
        return resp;

    }

    public PacketResponse deletePacket(String packetID){
        //rimuovo a db
        PacketDTO packet = new PacketDTO(packetID); //
        packetRepository.delete(packetRepository.findById(packet.getId()).orElse(new Packet())); //Come faccio a sapere che l'operazione è riuscita?
        //override di equals

        //popolo la response da riportatre al controller
        PacketResponse resp = new PacketResponse();
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Packet "+packet.getId()+" successfully eliminated!");
        resp.getPacket().setId(packet.getId());
        resp.setResult(result);
        return resp;
    }

    public PacketResponse findPacketById(String packetID){
        //cerco nel db
        Packet packet = packetRepository.findById(packetID)
                .orElseThrow(()->new RuntimeException("PacketNotFoundException"));

        //costruisco la risposta
        PacketResponse resp = new PacketResponse();
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Packet "+packet.getId()+" successfully found!");
        resp.setPacket(packet.toDTO());
        resp.setResult(result);
        return resp;
    }
}
