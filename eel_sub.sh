subscribe_Usage=$(cat <<EOF
usage: subscribe    [--action subscribe_unsubscribe]\
                    [--endpoint eel_endpoint]\
                    [--topic elements_topic]
EOF
)
function subscribe() {
    local action endpoint topic sub
    
	while [ "$1" != "" ]; do
      case $1 in
        -a | --action )
            shift
            action=$1
            ;;
        -e | --endpoint )
            shift
            endpoint=$1
            ;;
        -t | --topic )
            shift
            topic=$1
            ;;
        -h | --help )
            echo $subscribe_Usage
            return 0
            ;;
        * )
            echo $subscribe_Usage
            return 1
            ;;
      esac
      shift
    done

	if [ -z "$action" -o -z "$endpoint" -o -z "$topic" ]; then
        echo $subscribe_Usage
        return 1  
    fi
    
    if [ "subscribe" == "$action" ]; then
        sub="true"
    else
        sub="false"
    fi
    
    for try in {1..10}
    do
        local ret=$(curl -H "Content-Type: application/json" -X POST -s -d "{ \"topic\":\"$topic\", \"subscription\":$sub }" http://$endpoint/elements/topics)
        if [[ $ret == *"cannot reach elements"* ]]; then
            ecgi "retring..."
            sleep 1
        elif [[ $ret == *"subscribed"* ]]; then
            return 0
        fi
    done
    
    return 0
}