package com.group1.energymanager.service;

import com.group1.energymanager.DTOs.PacketDTO;
import com.group1.energymanager.exceptions.PacketNotFoundException;
import com.group1.energymanager.exceptions.UserNotFoundException;
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


    public PacketResponse addPacket(PacketRequest packetRequest) throws UserNotFoundException {
        PacketResponse resp=new PacketResponse();
        Packet packet = new Packet();

        // packet.setId("PAAAA0000"); //non va fatto, viene generato automaticamente
        //*prenderlo dal campo del packet request
        packet.setOwner(userRepository.findById(packetRequest.getOwner())
                .orElseThrow(()->new UserNotFoundException("User by id " + packetRequest.getOwner() + " was not found!")));
        packet.setTitle(packetRequest.getTitle());
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

    public PacketResponse updatePacket(PacketRequest packetRequest) throws PacketNotFoundException, UserNotFoundException {
        PacketResponse resp=new PacketResponse();

        //inserisco a db
        //Packet packet = new Packet(); //[PROVA] il frontend mi riempie gli altri campi che non sono stati modificati
        Packet packet = packetRepository.findById(packetRequest.getId())
                .orElseThrow(()->new PacketNotFoundException("Packet " + packetRequest.getId() + " was not found!"));
        //packet.setId(packetRequest.getId()); no perchè non voglio aggiornare l'id
        packet.setOwner(userRepository.findById(packetRequest.getOwner())
                .orElseThrow(()->new UserNotFoundException("User " + packetRequest.getOwner() + " was not found!"))); //da modificare con una vera eccezione
        packet.setTitle(packetRequest.getTitle());
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
        for(Packet p : packets) {
            PacketDTO tmp = new PacketDTO();
            tmp.setId(p.getId());
            tmp.setOwner(p.getOwner());
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

 /*
    public ListPacketResponse findAllPackets() {
        ListPacketResponse resp = new ListPacketResponse();
        List<Packet> packets = packetRepository.findAll();
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Found List of Packets!");
        resp.setListPacket(packets);
        resp.setResult(result);
        return resp;
    }

 */

    public PacketResponse deletePacket(String packetID) throws PacketNotFoundException {
        //rimuovo a db
        Packet removedPacket = packetRepository.findById(packetID)
                .orElseThrow(() -> new PacketNotFoundException("Packet " + packetID + " was not found!"));
         packetRepository.delete(removedPacket);
        //override di equals

        //popolo la response da riportatre al controller
        PacketResponse resp = new PacketResponse();
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Packet "+removedPacket.getId()+" successfully eliminated!");
        resp.getPacket().setId(removedPacket.getId());
        resp.setResult(result);
        return resp;
    }
    public PacketResponse findPacketById(String packetID) throws PacketNotFoundException {
        Packet packet = packetRepository.findById(packetID)
                .orElseThrow(() -> new PacketNotFoundException("Packet " + packetID + " was not found!"));
        //popolo response
        PacketResponse resp = new PacketResponse();
        BaseResponse result = new BaseResponse(HttpStatus.OK, "Packet "+packet.getId()+" successfully found!");
        resp.setPacket(packet.toDTO());
        resp.setResult(result);
        return resp;
    }
}
    /*

    public Optional<Packet> findPacketById(String packetID) {
        return findPacketById(packetID, true);
    }

    public Optional<Packet> findPacketById(String packetID, boolean checkSoftDeleted) {
        Optional<Packet> result = Optional.empty();
        Optional<Packet> packet = packetRepository.findById(packetID);
        //TODO controllare logica
        if (packet.isPresent() && (checkSoftDeleted && packet.get().getDeleted() || !checkSoftDeleted)) {
            result = packet;
        }
        return result;
    }

     */

