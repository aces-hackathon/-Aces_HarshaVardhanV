pragma solidity >=0.4.22 <0.9.0;

contract Report {
    string public name = 'Report';  //Name of the smart contract
    uint public reportCount = 0;
    mapping(uint => ReportInstance) public reports; //Mapping for the files

  struct ReportInstance
  {
    uint reportId;
    string userId;
    string reportHash;
    string description;
    string dept;
    string city;
    bool userStatus;
    bool adminStatus;
    uint uploadTime;
    address payable uploader;
  }

  event ReportUploaded( //Event to be emitted when a file is uploaded
    uint reportId,
    string userId,
    string reportHash,
    string description,
    string dept,
    string city,
    bool userStatus,
    bool adminStatus,
    uint uploadTime,
    address payable uploader
  );

  event ReportUpdated(
    uint reportId,
    string reportHash,
    bool userStatus,
    bool adminStatus,
    uint uploadTime
  );

  constructor() public {

  }

  //For uploading a file to the blockchain
  function reportProblem(
    string memory _reportHash,
    string memory _userId,
    string memory _description,
    string memory _dept,
    string memory _city,
    bool _userStatus,
    bool _adminStatus) public {
    
    require(bytes(_reportHash).length > 0); //File Hash should exist
    require(bytes(_description).length > 0); //File Description is given
    require(bytes(_dept).length > 0);
    require(msg.sender != address(0));  //File Uploader address exists

    //Actual File Uploading
    reports[reportCount] = ReportInstance(reportCount, _userId, _reportHash, _description, _dept, _city, _userStatus, _adminStatus, block.timestamp, msg.sender);
    reportCount++;

    emit ReportUploaded(reportCount, _userId, _reportHash, _description, _dept, _city, _userStatus, _adminStatus, block.timestamp, msg.sender);
  }

function adminSolved(string memory _reportHash) public {
      uint index = 0;
      for(uint i=0; i<=reportCount; i++) {
          if (keccak256(abi.encodePacked((reports[i].reportHash))) == keccak256(abi.encodePacked((_reportHash)))) {
              index = i;
              break;
          }
      }

      reports[index].adminStatus = true;
      emit ReportUpdated(index, reports[index].reportHash, reports[index].userStatus, reports[index].adminStatus, reports[index].uploadTime);
  }

  function userSolved(string memory _reportHash) public {
      uint index = 0;
      for(uint i=0; i<=reportCount; i++) {
          if (keccak256(abi.encodePacked((reports[i].reportHash))) == keccak256(abi.encodePacked((_reportHash)))) {
              index = i;
              break;
          }
      }

      reports[index].userStatus = true;
      emit ReportUpdated(index, reports[index].reportHash, reports[index].userStatus, reports[index].adminStatus, reports[index].uploadTime);
  }

  // function getReport(string memory city) public returns (ReportInstance [10] memory){
  //   ReportInstance[10] memory res;
  //   uint index=0;

  //   for(uint i=0; i<reportCount; i++) {
  //     if (keccak256(abi.encodePacked((reports[i].city))) == keccak256(abi.encodePacked((city)))) {
  //       res[index] = reports[i];
  //       index++;
  //     }

  //     if (index >= 10) {
  //       break;
  //     }
  //   }

  //   return res;
  // }
}