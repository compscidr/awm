<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ObservedDevice extends Model
{
    use HasFactory;
    protected $fillable = ['reporting_device_id', 'mac_address', 'mac_type', 'network_name', 'signal_strength', 'frequency', 'channel_width', 'security'];
}
