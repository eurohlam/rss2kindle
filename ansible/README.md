Ansible playbooks to deploy rss-2-kindle application
====================================================

## Prerequisites

* There is need to update hosts file before running ansible.
* Playbooks have been tested with ansible 2.7.10. In ansible 2.8 module docker_service has been renamed to docker_compose  

## To run deployment

    ansible-playbook -i hosts rss2kindle-deploy-playbook.yml
    
## To stop and remove application    

    ansible-playbook -i hosts rss2kindle-remove-playbook.yml